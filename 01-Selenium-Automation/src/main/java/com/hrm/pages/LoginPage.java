package com.hrm.pages;

import com.hrm.utils.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * LoginPage - Page Object for OrangeHRM Login screen.
 *
 * <p>Covers:
 * <ul>
 *   <li>Admin, HR Manager, Employee role logins</li>
 *   <li>Invalid credential validation</li>
 *   <li>Forgot password navigation</li>
 *   <li>Session timeout redirect</li>
 * </ul>
 *
 * @author Deep Ghevariya
 */
public class LoginPage {

    private static final Logger logger = LogManager.getLogger(LoginPage.class);
    private final WebDriver driver;
    private final WebDriverWait wait;

    // ════════════════════════════════════════════════
    //  WEB ELEMENTS — Page Factory
    // ════════════════════════════════════════════════

    @FindBy(name = "username")
    private WebElement usernameField;

    @FindBy(name = "password")
    private WebElement passwordField;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(css = ".oxd-alert-content-text")
    private WebElement errorMessage;

    @FindBy(css = ".orangehrm-login-forgot > p")
    private WebElement forgotPasswordLink;

    @FindBy(css = ".oxd-text--h5")
    private WebElement loginPageTitle;

    @FindBy(css = ".orangehrm-login-branding img")
    private WebElement logoImage;

    @FindBy(css = ".oxd-input--error")
    private WebElement inputErrorHighlight;

    // Password reset page elements
    @FindBy(name = "username")
    private WebElement resetUsernameField;

    @FindBy(css = "button[type='submit']")
    private WebElement resetSubmitButton;

    @FindBy(css = ".orangehrm-forgot-password-container .oxd-text")
    private WebElement resetSuccessMessage;

    // ════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ════════════════════════════════════════════════

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    // ════════════════════════════════════════════════
    //  PAGE ACTIONS
    // ════════════════════════════════════════════════

    /**
     * Perform a complete login with given credentials.
     *
     * @param username OrangeHRM username
     * @param password OrangeHRM password
     * @return DashboardPage after successful login
     */
    public DashboardPage login(String username, String password) {
        logger.info("Attempting login → user: {}", username);
        ExtentReportManager.logInfo("Logging in as: " + username);

        enterUsername(username);
        enterPassword(password);
        clickLoginButton();

        // Wait for dashboard to appear
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        logger.info("Login successful → user: {}", username);
        ExtentReportManager.logPass("Login successful for: " + username);

        return new DashboardPage(driver);
    }

    /**
     * Attempt login without expecting success (for negative tests).
     *
     * @param username invalid username
     * @param password invalid password
     */
    public void loginWithInvalidCredentials(String username, String password) {
        logger.info("Testing invalid login → user: {}", username);
        ExtentReportManager.logInfo("Testing invalid credentials: " + username);

        enterUsername(username);
        enterPassword(password);
        clickLoginButton();

        // Wait for error message
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameField));
        usernameField.clear();
        usernameField.sendKeys(username);
        logger.debug("Entered username: {}", username);
    }

    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
        logger.debug("Password entered");
    }

    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
        logger.debug("Login button clicked");
    }

    public void clickForgotPassword() {
        wait.until(ExpectedConditions.elementToBeClickable(forgotPasswordLink));
        forgotPasswordLink.click();
        logger.info("Navigated to Forgot Password page");
    }

    // ════════════════════════════════════════════════
    //  ASSERTIONS / GETTERS
    // ════════════════════════════════════════════════

    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        String msg = errorMessage.getText();
        logger.debug("Error message: {}", msg);
        return msg;
    }

    public boolean isErrorMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoginPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(usernameField));
            return usernameField.isDisplayed() && loginButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLogoVisible() {
        try {
            return logoImage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getPageTitle() {
        try {
            wait.until(ExpectedConditions.visibilityOf(loginPageTitle));
            return loginPageTitle.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isUsernameFieldEmpty() {
        return usernameField.getAttribute("value").isEmpty();
    }

    public boolean isPasswordFieldEmpty() {
        return passwordField.getAttribute("value").isEmpty();
    }

    public boolean hasFieldValidationError() {
        try {
            return inputErrorHighlight.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
