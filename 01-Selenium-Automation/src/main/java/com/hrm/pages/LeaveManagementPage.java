package com.hrm.pages;

import com.hrm.utils.ExtentReportManager;
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
 * LeaveManagementPage - Page Object for OrangeHRM Leave Module.
 *
 * <p>Covers the full Leave workflow:
 * Apply → Submit → Manager Approval/Rejection → Leave Balance Check
 *
 * @author Deep Ghevariya
 */
public class LeaveManagementPage {

    private static final Logger logger = LogManager.getLogger(LeaveManagementPage.class);
    private final WebDriver driver;
    private final WebDriverWait wait;

    // ════════════════════════════════════════════════
    //  APPLY LEAVE FORM
    // ════════════════════════════════════════════════

    @FindBy(css = ".oxd-select-text-input")
    private List<WebElement> dropdowns;

    @FindBy(css = "input.oxd-date-input-field")
    private List<WebElement> dateInputs;

    @FindBy(css = ".oxd-textarea")
    private WebElement commentsTextarea;

    @FindBy(css = "button[type='submit']")
    private WebElement applyButton;

    // ════════════════════════════════════════════════
    //  LEAVE LIST
    // ════════════════════════════════════════════════

    @FindBy(css = ".oxd-table-body .oxd-table-row")
    private List<WebElement> leaveTableRows;

    @FindBy(css = ".oxd-table-cell .oxd-badge--success")
    private List<WebElement> approvedBadges;

    @FindBy(css = ".oxd-table-cell .oxd-badge--warn")
    private List<WebElement> pendingBadges;

    @FindBy(css = ".oxd-table-cell .oxd-badge--danger")
    private List<WebElement> rejectedBadges;

    // ════════════════════════════════════════════════
    //  APPROVE / REJECT ACTIONS
    // ════════════════════════════════════════════════

    @FindBy(css = "button.oxd-button--success")
    private WebElement approveButton;

    @FindBy(css = "button.oxd-button--danger")
    private WebElement rejectButton;

    // ════════════════════════════════════════════════
    //  LEAVE BALANCE
    // ════════════════════════════════════════════════

    @FindBy(css = ".orangehrm-leave-balance-table .orangehrm-leave-balance")
    private List<WebElement> leaveBalanceCells;

    // Toast
    @FindBy(css = ".oxd-toast-content-text")
    private WebElement toastText;

    public LeaveManagementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    // ════════════════════════════════════════════════
    //  NAVIGATION
    // ════════════════════════════════════════════════

    public void navigateToApplyLeave() {
        driver.get(driver.getCurrentUrl().split("/web")[0]
                + "/web/index.php/leave/applyHRMLeave");
        wait.until(ExpectedConditions.urlContains("applyHRMLeave"));
        logger.info("Navigated to Apply Leave page");
    }

    public void navigateToMyLeaveList() {
        driver.get(driver.getCurrentUrl().split("/web")[0]
                + "/web/index.php/leave/viewMyLeaveList");
        wait.until(ExpectedConditions.urlContains("viewMyLeaveList"));
        logger.info("Navigated to My Leave List");
    }

    public void navigateToLeaveList() {
        driver.get(driver.getCurrentUrl().split("/web")[0]
                + "/web/index.php/leave/viewLeaveList");
        wait.until(ExpectedConditions.urlContains("viewLeaveList"));
        logger.info("Navigated to Leave List (Admin view)");
    }

    public void navigateToLeaveBalance() {
        driver.get(driver.getCurrentUrl().split("/web")[0]
                + "/web/index.php/leave/viewLeaveBalanceReport");
        logger.info("Navigated to Leave Balance Report");
    }

    // ════════════════════════════════════════════════
    //  APPLY LEAVE
    // ════════════════════════════════════════════════

    public void selectLeaveType(String leaveType) {
        logger.info("Selecting leave type: {}", leaveType);
        wait.until(ExpectedConditions.elementToBeClickable(dropdowns.get(0))).click();
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@role='listbox']//span[text()='" + leaveType + "']")));
        option.click();
        logger.debug("Leave type selected: {}", leaveType);
    }

    public void enterFromDate(String date) {
        logger.info("Setting from date: {}", date);
        wait.until(ExpectedConditions.visibilityOf(dateInputs.get(0)));
        dateInputs.get(0).clear();
        dateInputs.get(0).sendKeys(date);
        dateInputs.get(0).sendKeys(Keys.TAB); // trigger date validation
    }

    public void enterToDate(String date) {
        logger.info("Setting to date: {}", date);
        dateInputs.get(1).clear();
        dateInputs.get(1).sendKeys(date);
        dateInputs.get(1).sendKeys(Keys.TAB);
    }

    public void enterComments(String comments) {
        wait.until(ExpectedConditions.visibilityOf(commentsTextarea));
        commentsTextarea.clear();
        commentsTextarea.sendKeys(comments);
    }

    public void clickApply() {
        wait.until(ExpectedConditions.elementToBeClickable(applyButton)).click();
        logger.info("Leave application submitted");
        ExtentReportManager.logInfo("Leave application submitted");
    }

    /**
     * Complete leave application workflow in one call.
     *
     * @param leaveType "CasualLeave", "SickLeave", etc.
     * @param fromDate  "yyyy-mm-dd" format
     * @param toDate    "yyyy-mm-dd" format
     * @param comments  reason/remarks
     */
    public void applyLeave(String leaveType, String fromDate, String toDate, String comments) {
        navigateToApplyLeave();
        selectLeaveType(leaveType);
        enterFromDate(fromDate);
        enterToDate(toDate);
        enterComments(comments);
        clickApply();
        ExtentReportManager.logPass("Leave applied: " + leaveType + " from " + fromDate + " to " + toDate);
    }

    // ════════════════════════════════════════════════
    //  APPROVE / REJECT
    // ════════════════════════════════════════════════

    public void approveLeaveByRow(int rowIndex) {
        logger.info("Approving leave at row: {}", rowIndex);
        List<WebElement> rows = wait.until(ExpectedConditions.visibilityOfAllElements(leaveTableRows));
        WebElement approveBtn = rows.get(rowIndex).findElement(
                By.cssSelector("button.oxd-button--success"));
        approveBtn.click();
        waitForToast();
        ExtentReportManager.logPass("Leave approved at row: " + rowIndex);
    }

    public void rejectLeaveByRow(int rowIndex) {
        logger.info("Rejecting leave at row: {}", rowIndex);
        List<WebElement> rows = wait.until(ExpectedConditions.visibilityOfAllElements(leaveTableRows));
        WebElement rejectBtn = rows.get(rowIndex).findElement(
                By.cssSelector("button.oxd-button--danger"));
        rejectBtn.click();
        waitForToast();
        ExtentReportManager.logPass("Leave rejected at row: " + rowIndex);
    }

    // ════════════════════════════════════════════════
    //  GETTERS
    // ════════════════════════════════════════════════

    public int getPendingLeaveCount() {
        return pendingBadges.size();
    }

    public int getApprovedLeaveCount() {
        return approvedBadges.size();
    }

    public int getRejectedLeaveCount() {
        return rejectedBadges.size();
    }

    public String getToastMessage() {
        wait.until(ExpectedConditions.visibilityOf(toastText));
        return toastText.getText();
    }

    public boolean isLeaveApplicationSuccessful() {
        try {
            wait.until(ExpectedConditions.urlContains("viewMyLeaveList"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void waitForToast() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".oxd-toast")));
        } catch (TimeoutException ignored) {}
    }
}
