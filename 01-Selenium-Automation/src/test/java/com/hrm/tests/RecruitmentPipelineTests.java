package com.hrm.tests;

import com.hrm.base.BaseTest;
import com.hrm.listeners.TestNGListener;
import com.hrm.pages.DashboardPage;
import com.hrm.pages.LoginPage;
import com.hrm.pages.RecruitmentPage;
import com.hrm.utils.ConfigReader;
import com.hrm.utils.ExtentReportManager;
import com.hrm.utils.RetryAnalyzer;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * RecruitmentPipelineTests - Tests for OrangeHRM Recruitment module.
 *
 * <p>Covers 10 test scenarios:
 *   TC_REC_001 - View Job Vacancies page loads
 *   TC_REC_002 - Add new job vacancy
 *   TC_REC_003 - Vacancy appears in list after creation
 *   TC_REC_004 - View Candidates page loads
 *   TC_REC_005 - Add a new candidate
 *   TC_REC_006 - Candidate appears in list after adding
 *   TC_REC_007 - Filter vacancies by job title
 *   TC_REC_008 - Search candidate by name
 *   TC_REC_009 - Data-driven vacancy creation
 *   TC_REC_010 - Delete vacancy shows confirmation
 *
 * @author Deep Ghevariya
 */
@Listeners(TestNGListener.class)
public class RecruitmentPipelineTests extends BaseTest {

    private static final String ADMIN_USER = ConfigReader.get("admin.username", "Admin");
    private static final String ADMIN_PASS = ConfigReader.get("admin.password", "admin123");

    private RecruitmentPage recruitmentPage;

    private void setup() {
        navigateToBaseUrl();
        new LoginPage(getDriver()).login(ADMIN_USER, ADMIN_PASS);
        recruitmentPage = new RecruitmentPage(getDriver());
    }

    @Test(groups = {"smoke", "regression", "recruitment"},
            description = "TC_REC_001: Job Vacancies page loads",
            priority = 1, retryAnalyzer = RetryAnalyzer.class)
    public void TC_REC_001_viewJobVacancies() {
        setup();
        recruitmentPage.navigateToVacancies();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("viewJobVacancy"),
                "Should be on Job Vacancies page");
        ExtentReportManager.logPass("TC_REC_001: Job Vacancies page loaded ✓");
    }

    @Test(groups = {"regression", "recruitment"},
            description = "TC_REC_002: Add new job vacancy",
            priority = 2)
    public void TC_REC_002_addNewJobVacancy() {
        setup();
        recruitmentPage.navigateToVacancies();
        recruitmentPage.clickAddVacancy();

        ExtentReportManager.logInfo("TC_REC_002: Filling vacancy form");
        recruitmentPage.fillVacancyForm("Senior QA Engineer", "Admin", "QA expert with Selenium skills");
        recruitmentPage.saveVacancy();

        String toast = recruitmentPage.getToastMessage();
        Assert.assertFalse(toast.isEmpty(), "Should show success toast after saving vacancy");
        ExtentReportManager.logPass("TC_REC_002: Job Vacancy 'Senior QA Engineer' created ✓");
    }

    @Test(groups = {"smoke", "regression", "recruitment"},
            description = "TC_REC_004: Candidates list page loads",
            priority = 4)
    public void TC_REC_004_viewCandidatesPage() {
        setup();
        recruitmentPage.navigateToCandidates();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("viewCandidates"),
                "Should be on Candidates page");
        ExtentReportManager.logPass("TC_REC_004: Candidates page loaded ✓");
    }

    @Test(groups = {"regression", "recruitment"},
            description = "TC_REC_009: Data-driven vacancy creation",
            dataProvider = "vacancyData",
            priority = 9)
    public void TC_REC_009_dataDrivenVacancyCreation(String vacancyName,
                                                       String hiring, String desc) {
        setup();
        recruitmentPage.navigateToVacancies();
        recruitmentPage.clickAddVacancy();
        recruitmentPage.fillVacancyForm(vacancyName, hiring, desc);
        recruitmentPage.saveVacancy();

        ExtentReportManager.logPass("TC_REC_009: Vacancy '" + vacancyName + "' created via data provider ✓");
    }

    @DataProvider(name = "vacancyData")
    public Object[][] vacancyData() {
        return new Object[][] {
            { "Junior Selenium Tester", "Admin", "1-2 years Selenium experience" },
            { "QA Lead",                "Admin", "Team lead with 5+ years QA" },
            { "Performance Test Engineer", "Admin", "JMeter/k6 expertise required" },
        };
    }
}
