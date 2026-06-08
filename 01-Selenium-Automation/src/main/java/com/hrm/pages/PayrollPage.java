package com.hrm.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * PayrollPage - Page Object for OrangeHRM Payroll (Admin Payroll Configuration).
 *
 * @author Deep Ghevariya
 */
public class PayrollPage {

    private static final Logger logger = LogManager.getLogger(PayrollPage.class);
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = ".oxd-table-body .oxd-table-row")
    private List<WebElement> payrollTableRows;

    @FindBy(css = "button[type='submit']")
    private WebElement submitButton;

    @FindBy(css = ".oxd-input")
    private List<WebElement> inputFields;

    @FindBy(css = ".oxd-toast-content-text")
    private WebElement toastMessage;

    @FindBy(css = ".oxd-select-text-input")
    private List<WebElement> selectDropdowns;

    @FindBy(css = ".orangehrm-payroll-tax-amount")
    private WebElement taxAmountDisplay;

    public PayrollPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public void navigateToPayGrades() {
        driver.get(driver.getCurrentUrl().split("/web")[0]
                + "/web/index.php/admin/viewPayGrades");
        wait.until(ExpectedConditions.urlContains("viewPayGrades"));
        logger.info("Navigated to Pay Grades");
    }

    public void navigateToPayroll() {
        driver.get(driver.getCurrentUrl().split("/web")[0]
                + "/web/index.php/payroll/viewPayrollModule");
        logger.info("Navigated to Payroll Module");
    }

    public void addPayGrade(String gradeName, String currency, double minSalary, double maxSalary) {
        logger.info("Adding pay grade: {} | {}–{} {}", gradeName, minSalary, maxSalary, currency);

        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.oxd-button--secondary")));
        addBtn.click();

        wait.until(ExpectedConditions.visibilityOf(inputFields.get(0)));
        inputFields.get(0).sendKeys(gradeName);

        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit']")));
        saveBtn.click();

        wait.until(ExpectedConditions.visibilityOf(toastMessage));
        logger.info("Pay grade '{}' added", gradeName);
    }

    public int getPayGradeCount() {
        return payrollTableRows.size();
    }

    public String getToastMessage() {
        wait.until(ExpectedConditions.visibilityOf(toastMessage));
        return toastMessage.getText();
    }

    /**
     * Calculate expected gross salary.
     * Formula: basicSalary + HRA + conveyance - providentFund - tax
     */
    public double calculateGrossSalary(double basic, double hra, double conveyance,
                                       double providentFund, double tax) {
        double gross = basic + hra + conveyance - providentFund - tax;
        logger.info("Gross salary calculation: {} + {} + {} - {} - {} = {}",
                basic, hra, conveyance, providentFund, tax, gross);
        return gross;
    }

    /**
     * Calculate PF deduction (standard Indian rate: 12% of basic)
     */
    public double calculatePFDeduction(double basicSalary) {
        return Math.round(basicSalary * 0.12 * 100.0) / 100.0;
    }

    /**
     * Calculate income tax (simplified slab for demo)
     * Slab: 0-2.5L = 0%, 2.5-5L = 5%, 5-10L = 20%, 10L+ = 30%
     */
    public double calculateIncomeTax(double annualSalary) {
        if (annualSalary <= 250000) return 0;
        if (annualSalary <= 500000) return (annualSalary - 250000) * 0.05;
        if (annualSalary <= 1000000) return 12500 + (annualSalary - 500000) * 0.20;
        return 112500 + (annualSalary - 1000000) * 0.30;
    }
}
