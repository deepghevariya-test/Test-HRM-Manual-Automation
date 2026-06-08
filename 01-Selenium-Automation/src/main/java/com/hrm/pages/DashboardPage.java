package com.hrm.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * DashboardPage - Page Object for OrangeHRM Dashboard.
 *
 * @author Deep Ghevariya
 */
public class DashboardPage {

    private static final Logger logger = LogManager.getLogger(DashboardPage.class);
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = ".oxd-topbar-header-breadcrumb > .oxd-text")
    private WebElement pageHeader;

    @FindBy(css = ".oxd-userdropdown-name")
    private WebElement loggedInUserName;

    @FindBy(css = ".oxd-userdropdown-tab")
    private WebElement userDropdown;

    @FindBy(css = "a[href='/web/index.php/auth/logout']")
    private WebElement logoutOption;

    @FindBy(css = ".oxd-sidepanel-body .oxd-nav-item")
    private List<WebElement> navMenuItems;

    @FindBy(css = ".orangehrm-dashboard-widget-name")
    private List<WebElement> dashboardWidgets;

    @FindBy(css = ".oxd-main-menu-item--name")
    private List<WebElement> mainMenuLabels;

    // Quick launch buttons
    @FindBy(css = ".oxd-quick-launch-card")
    private List<WebElement> quickLaunchCards;

    // Subheader links
    @FindBy(linkText = "My Info")
    private WebElement myInfoLink;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public boolean isDashboardLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageHeader));
            return driver.getCurrentUrl().contains("/dashboard");
        } catch (Exception e) {
            return false;
        }
    }

    public String getPageHeader() {
        wait.until(ExpectedConditions.visibilityOf(pageHeader));
        return pageHeader.getText();
    }

    public String getLoggedInUser() {
        wait.until(ExpectedConditions.visibilityOf(loggedInUserName));
        return loggedInUserName.getText();
    }

    public void logout() {
        logger.info("Logging out");
        wait.until(ExpectedConditions.elementToBeClickable(userDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(logoutOption)).click();
        wait.until(ExpectedConditions.urlContains("/auth/login"));
        logger.info("Logged out successfully");
    }

    public int getWidgetCount() {
        return dashboardWidgets.size();
    }

    public int getQuickLaunchCount() {
        return quickLaunchCards.size();
    }

    public boolean isMenuItemPresent(String menuName) {
        return mainMenuLabels.stream()
                .anyMatch(el -> el.getText().trim().equalsIgnoreCase(menuName));
    }

    public void navigateToMenu(String menuName) {
        logger.info("Navigating to menu: {}", menuName);
        mainMenuLabels.stream()
                .filter(el -> el.getText().trim().equalsIgnoreCase(menuName))
                .findFirst()
                .ifPresent(WebElement::click);
    }

    public List<String> getAllMenuItems() {
        return mainMenuLabels.stream()
                .map(el -> el.getText().trim())
                .toList();
    }
}
