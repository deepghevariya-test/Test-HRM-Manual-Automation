package com.hrm.tests;

import com.hrm.base.BaseTest;
import com.hrm.pages.DashboardPage;
import com.hrm.pages.LoginPage;
import com.hrm.utils.ConfigReader;
import com.hrm.utils.ExtentReportManager;
import com.hrm.utils.RetryAnalyzer;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.hrm.listeners.TestNGListener;

/**
 * LoginTests - Tests for OrangeHRM Authentication module.
 *
 * <p>Covers 12 test scenarios:
 * <ul>
 *   <li>TC_LGN_001 - Valid admin login</li>
 *   <li>TC_LGN_002 - Login page elements visible</li>
 *   <li>TC_LGN_003 - Invalid username shows error</li>
 *   <li>TC_LGN_004 - Invalid password shows error</li>
 *   <li>TC_LGN_005 - Empty credentials validation</li>
 *   <li>TC_LGN_006 - Empty username validation</li>
 *   <li>TC_LGN_007 - Empty password validation</li>
 *   <li>TC_LGN_008 - SQL injection attempt</li>
 *   <li>TC_LGN_009 - Logout functionality</li>
 *   <li>TC_LGN_010 - Back button after logout</li>
 *   <li>TC_LGN_011 - Forgot password navigation</li>
 *   <li>TC_LGN_012 - Case sensitivity check</li>
 * </ul>
 *
 * @author Deep Ghevariya
 */
@Listeners(TestNGListener.class)
public class LoginTests extends BaseTest {

    private static final String VALID_USERNAME = ConfigReader.get("admin.username", "Admin");
    private static final String VALID_PASSWORD = ConfigReader.get("admin.password", "admin123");

    // ════════════════════════════════════════════════
    //  POSITIVE TEST CASES
    // ════════════════════════════════════════════════

    /**
     * TC_LGN_001 - Verify admin can log in with valid credentials.
     * Priority: Critical | Type: Smoke
     */
    @Test(groups = {"smoke", "regression", "login"},
            description = "TC_LGN_001: Valid admin login redirects to Dashboard",
            retryAnalyzer = RetryAnalyzer.class)
    public void TC_LGN_001_validAdminLogin() {
        ExtentReportManager.logInfo("Test: Valid Admin Login");

        navigateToBaseUrl();
        LoginPage loginPage = new LoginPage(getDriver());

        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "Login page should be displayed before login");
        ExtentReportManager.logPass("Login page displayed correctly");

        DashboardPage dashboard = loginPage.login(VALID_USERNAME, VALID_PASSWORD);

        Assert.assertTrue(dashboard.isDashboardLoaded(),
                "Dashboard should be loaded after successful login");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/dashboard"),
                "URL should contain '/dashboard' after login");

        ExtentReportManager.logPass("Admin login successful. Dashboard loaded. URL: " + getDriver().getCurrentUrl());
    }

    /**
     * TC_LGN_002 - Verify login page elements are present.
     * Priority: High | Type: Regression
     */
    @Test(groups = {"regression", "login"},
            description = "TC_LGN_002: Verify all login page UI elements are displayed")
    public void TC_LGN_002_loginPageElementsVisible() {
        navigateToBaseUrl();
        LoginPage loginPage = new LoginPage(getDriver());

        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login form should be visible");
        Assert.assertTrue(loginPage.isLogoVisible(), "OrangeHRM logo should be visible");
        ExtentReportManager.logPass("All login page elements verified");
    }

    /**
     * TC_LGN_009 - Verify logout functionality.
     * Priority: High | Type: Smoke
     */
    @Test(groups = {"smoke", "regression", "login"},
            description = "TC_LGN_009: Verify user can logout successfully",
            retryAnalyzer = RetryAnalyzer.class)
    public void TC_LGN_009_logoutFunctionality() {
        navigateToBaseUrl();
        LoginPage loginPage = new LoginPage(getDriver());
        DashboardPage dashboard = loginPage.login(VALID_USERNAME, VALID_PASSWORD);

        Assert.assertTrue(dashboard.isDashboardLoaded(), "Should be on dashboard before logout");

        dashboard.logout();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("/auth/login"),
                "After logout, should be redirected to login page");
        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "Login page should be displayed after logout");

        ExtentReportManager.logPass("Logout successful. Redirected to login page.");
    }

    /**
     * TC_LGN_011 - Verify Forgot Password navigation.
     * Priority: Medium | Type: Regression
     */
    @Test(groups = {"regression", "login"},
            description = "TC_LGN_011: Forgot Password link navigates correctly")
    public void TC_LGN_011_forgotPasswordNavigation() {
        navigateToBaseUrl();
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.clickForgotPassword();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("requestPasswordResetCode")
                        || getDriver().getCurrentUrl().contains("forgotPassword"),
                "Should navigate to forgot password page");
        ExtentReportManager.logPass("Forgot password navigation works correctly");
    }

    // ════════════════════════════════════════════════
    //  NEGATIVE TEST CASES
    // ════════════════════════════════════════════════

    /**
     * TC_LGN_003 to TC_LGN_008 - Negative login tests using DataProvider.
     * Covers: invalid creds, empty fields, SQL injection, case sensitivity
     */
    @Test(groups = {"regression", "login", "negative"},
            description = "TC_LGN_003–008: Invalid credentials show error messages",
            dataProvider = "invalidLoginData")
    public void TC_LGN_003_008_invalidLoginScenarios(String testId, String username,
                                                      String password, String expectedError) {
        ExtentReportManager.logInfo(testId + " → username='" + username + "'");

        navigateToBaseUrl();
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.loginWithInvalidCredentials(username, password);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                testId + ": Error message should be displayed for invalid credentials");

        String actualError = loginPage.getErrorMessage();
        Assert.assertFalse(actualError.isEmpty(),
                testId + ": Error message text should not be empty");

        ExtentReportManager.logPass(testId + " passed — Error shown: '" + actualError + "'");
    }

    /**
     * TC_LGN_005 - Empty username and password validation.
     */
    @Test(groups = {"regression", "login", "negative"},
            description = "TC_LGN_005: Empty credentials should show validation error")
    public void TC_LGN_005_emptyCredentials() {
        navigateToBaseUrl();
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.enterUsername("");
        loginPage.enterPassword("");
        loginPage.clickLoginButton();

        // Should show Required field validation errors
        Assert.assertTrue(loginPage.isErrorMessageDisplayed() || loginPage.hasFieldValidationError(),
                "Empty credentials should trigger validation error");
        ExtentReportManager.logPass("Empty credentials validation works correctly");
    }

    /**
     * TC_LGN_010 - Session: back button after logout should not re-authenticate.
     */
    @Test(groups = {"regression", "login", "security"},
            description = "TC_LGN_010: Browser back button after logout should not grant access",
            retryAnalyzer = RetryAnalyzer.class)
    public void TC_LGN_010_backButtonAfterLogout() {
        navigateToBaseUrl();
        LoginPage loginPage = new LoginPage(getDriver());
        DashboardPage dashboard = loginPage.login(VALID_USERNAME, VALID_PASSWORD);
        Assert.assertTrue(dashboard.isDashboardLoaded());

        dashboard.logout();

        // Try navigating back
        getDriver().navigate().back();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // Should be redirected to login page (session invalidation)
        boolean onLogin = getDriver().getCurrentUrl().contains("/auth/login")
                || getDriver().getCurrentUrl().contains("/auth/login");
        Assert.assertTrue(onLogin,
                "After logout, back navigation should not restore the authenticated session");
        ExtentReportManager.logPass("Session correctly invalidated after logout");
    }

    /**
     * TC_LGN_012 - Username case sensitivity check.
     */
    @Test(groups = {"regression", "login", "negative"},
            description = "TC_LGN_012: Username with wrong case should fail login")
    public void TC_LGN_012_usernameCaseSensitivity() {
        navigateToBaseUrl();
        LoginPage loginPage = new LoginPage(getDriver());

        // OrangeHRM is case-sensitive for username
        loginPage.loginWithInvalidCredentials("ADMIN", VALID_PASSWORD);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Login with wrong-case username should fail");
        ExtentReportManager.logPass("Username case sensitivity verified — ADMIN ≠ Admin");
    }

    // ════════════════════════════════════════════════
    //  DATA PROVIDERS
    // ════════════════════════════════════════════════

    @DataProvider(name = "invalidLoginData")
    public Object[][] invalidLoginData() {
        return new Object[][] {
            // testId, username, password, expected error hint
            { "TC_LGN_003", "InvalidUser",    VALID_PASSWORD, "Invalid credentials" },
            { "TC_LGN_004", VALID_USERNAME,   "wrongpassword123", "Invalid credentials" },
            { "TC_LGN_006", "",               VALID_PASSWORD, "Required field" },
            { "TC_LGN_007", VALID_USERNAME,   "", "Required field" },
            { "TC_LGN_008", "' OR '1'='1",   "' OR '1'='1", "Invalid credentials (SQL injection rejected)" },
        };
    }
}
