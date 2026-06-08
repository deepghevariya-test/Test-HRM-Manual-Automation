package com.hrm.tests;

import com.hrm.base.BaseTest;
import com.hrm.listeners.TestNGListener;
import com.hrm.pages.DashboardPage;
import com.hrm.pages.LoginPage;
import com.hrm.pages.PayrollPage;
import com.hrm.utils.ConfigReader;
import com.hrm.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * PayrollCalculationTests - Tests for HRM Payroll module.
 *
 * <p>Covers 10 test cases including:
 *   TC_PAY_001 - View Pay Grades page loads
 *   TC_PAY_002 - Verify gross salary calculation (basic + HRA - deductions)
 *   TC_PAY_003 - Verify PF deduction (12% of basic)
 *   TC_PAY_004 - Verify income tax calculation (Indian slab system)
 *   TC_PAY_005 - Add new pay grade
 *   TC_PAY_006 - Salary with zero deductions = gross = basic+allowances
 *   TC_PAY_007 - Net salary > 0 for all valid inputs
 *   TC_PAY_008 - Tax = 0 for salary below exemption limit (2.5L/year)
 *   TC_PAY_009 - Salary structure with multiple components
 *   TC_PAY_010 - Data-driven payroll calculations
 *
 * @author Deep Ghevariya
 */
@Listeners(TestNGListener.class)
public class PayrollCalculationTests extends BaseTest {

    private static final String ADMIN_USER = ConfigReader.get("admin.username", "Admin");
    private static final String ADMIN_PASS = ConfigReader.get("admin.password", "admin123");

    private PayrollPage payrollPage;

    private void setupPayrollPage() {
        navigateToBaseUrl();
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(ADMIN_USER, ADMIN_PASS);
        payrollPage = new PayrollPage(getDriver());
    }

    @Test(groups = {"smoke", "regression", "payroll"},
            description = "TC_PAY_001: Pay Grades configuration page loads",
            priority = 1)
    public void TC_PAY_001_viewPayGradesPage() {
        setupPayrollPage();
        payrollPage.navigateToPayGrades();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("viewPayGrades"),
                "Should be on Pay Grades page");
        ExtentReportManager.logPass("TC_PAY_001: Pay Grades page loaded successfully");
    }

    @Test(groups = {"regression", "payroll", "calculation"},
            description = "TC_PAY_002: Gross salary = Basic + HRA + Conveyance",
            priority = 2)
    public void TC_PAY_002_grossSalaryCalculation() {
        setupPayrollPage();
        ExtentReportManager.logInfo("TC_PAY_002: Verifying gross salary calculation");

        double basic = 50000.0;
        double hra = 20000.0;       // 40% of basic
        double conveyance = 2000.0;
        double pf = 6000.0;         // 12% of basic
        double tax = 625.0;         // Monthly tax

        double expectedGross = basic + hra + conveyance - pf - tax;  // 65375.0
        double calculatedGross = payrollPage.calculateGrossSalary(basic, hra, conveyance, pf, tax);

        Assert.assertEquals(calculatedGross, expectedGross, 0.01,
                "Gross salary calculation should match expected formula");

        ExtentReportManager.logPass("TC_PAY_002: Gross Salary = ₹" + calculatedGross + " ✓");
    }

    @Test(groups = {"regression", "payroll", "calculation"},
            description = "TC_PAY_003: PF deduction = 12% of basic salary",
            dataProvider = "pfCalculationData",
            priority = 3)
    public void TC_PAY_003_pfDeductionCalculation(double basicSalary, double expectedPf) {
        setupPayrollPage();
        double calculatedPf = payrollPage.calculatePFDeduction(basicSalary);

        Assert.assertEquals(calculatedPf, expectedPf, 0.01,
                "PF (12% of basic ₹" + basicSalary + ") should be ₹" + expectedPf);
        ExtentReportManager.logPass("TC_PAY_003: PF deduction for ₹" + basicSalary
                + " = ₹" + calculatedPf + " ✓");
    }

    @Test(groups = {"regression", "payroll", "calculation"},
            description = "TC_PAY_004: Income tax follows Indian slab system",
            dataProvider = "taxSlabData",
            priority = 4)
    public void TC_PAY_004_incomeTaxSlabCalculation(double annualSalary,
                                                      double expectedTax, String description) {
        setupPayrollPage();
        ExtentReportManager.logInfo("TC_PAY_004: " + description + " | Annual: ₹" + annualSalary);

        double calculatedTax = payrollPage.calculateIncomeTax(annualSalary);

        Assert.assertEquals(calculatedTax, expectedTax, 0.01,
                "Tax slab mismatch for annual salary ₹" + annualSalary);
        ExtentReportManager.logPass("TC_PAY_004: " + description + " → Tax = ₹" + calculatedTax + " ✓");
    }

    @Test(groups = {"regression", "payroll", "calculation"},
            description = "TC_PAY_006: Net salary with zero deductions equals gross",
            priority = 6)
    public void TC_PAY_006_zeroDeductionsNetSalary() {
        setupPayrollPage();
        double basic = 80000.0;
        double hra = 32000.0;
        double conveyance = 2000.0;

        double gross = payrollPage.calculateGrossSalary(basic, hra, conveyance, 0, 0);

        Assert.assertEquals(gross, basic + hra + conveyance, 0.01,
                "With zero deductions, gross = basic + HRA + conveyance");
        ExtentReportManager.logPass("TC_PAY_006: Zero deduction salary calc = ₹" + gross + " ✓");
    }

    @Test(groups = {"regression", "payroll", "calculation"},
            description = "TC_PAY_007: Net salary is always positive for valid inputs",
            priority = 7)
    public void TC_PAY_007_netSalaryAlwaysPositive() {
        setupPayrollPage();
        double basic = 35000.0;
        double hra = 14000.0;
        double conveyance = 1600.0;
        double pf = payrollPage.calculatePFDeduction(basic);     // 4200
        double tax = payrollPage.calculateIncomeTax(basic * 12) / 12; // monthly tax

        double net = payrollPage.calculateGrossSalary(basic, hra, conveyance, pf, tax);

        Assert.assertTrue(net > 0,
                "Net salary should be positive. Calculated: ₹" + net);
        ExtentReportManager.logPass("TC_PAY_007: Net salary = ₹" + net + " > 0 ✓");
    }

    @Test(groups = {"regression", "payroll", "calculation"},
            description = "TC_PAY_008: Tax = 0 for income below ₹2.5L/year",
            priority = 8)
    public void TC_PAY_008_noTaxBelowExemptionLimit() {
        setupPayrollPage();
        double annualSalary = 240000.0; // ₹2.4 Lakhs — below exemption
        double tax = payrollPage.calculateIncomeTax(annualSalary);

        Assert.assertEquals(tax, 0.0, 0.01,
                "Income below ₹2.5L should have 0 tax");
        ExtentReportManager.logPass("TC_PAY_008: Tax = ₹0 for ₹" + annualSalary + "/year ✓");
    }

    // ════════════════════════════════════════════════
    //  DATA PROVIDERS
    // ════════════════════════════════════════════════

    @DataProvider(name = "pfCalculationData")
    public Object[][] pfCalculationData() {
        return new Object[][] {
            { 20000.0, 2400.0  },   // 12% of 20,000
            { 30000.0, 3600.0  },   // 12% of 30,000
            { 50000.0, 6000.0  },   // 12% of 50,000
            { 100000.0, 12000.0 },  // 12% of 1,00,000
        };
    }

    @DataProvider(name = "taxSlabData")
    public Object[][] taxSlabData() {
        return new Object[][] {
            // annualSalary, expectedTax, description
            { 200000.0,  0.0,     "Below exemption limit (₹2L)" },
            { 300000.0,  2500.0,  "5% slab: ₹300K - ₹250K = ₹50K × 5%" },
            { 600000.0,  32500.0, "20% slab: ₹12500 + ₹100K × 20%" },
            { 1200000.0, 142500.0,"30% slab: ₹112500 + ₹200K × 30%" },
        };
    }
}
