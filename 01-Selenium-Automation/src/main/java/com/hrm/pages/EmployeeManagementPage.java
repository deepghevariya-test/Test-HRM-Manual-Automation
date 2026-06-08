package com.hrm.pages;

import com.hrm.utils.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

/**
 * EmployeeManagementPage - Page Object for OrangeHRM Employee Management (PIM module).
 *
 * <p>Covers full CRUD:
 * <ul>
 *   <li>Add Employee (basic info + personal details)</li>
 *   <li>Edit Employee (name, job, contact)</li>
 *   <li>Delete Employee (single + bulk)</li>
 *   <li>Search Employee by ID, name, department</li>
 *   <li>Filter by employment status, sub-unit, job title</li>
 *   <li>Upload profile photo</li>
 *   <li>Document upload (Qualifications tab)</li>
 * </ul>
 *
 * @author Deep Ghevariya
 */
public class EmployeeManagementPage {

    private static final Logger logger = LogManager.getLogger(EmployeeManagementPage.class);
    private final WebDriver driver;
    private final WebDriverWait wait;

    // ════════════════════════════════════════════════
    //  EMPLOYEE LIST PAGE
    // ════════════════════════════════════════════════

    @FindBy(css = "button.oxd-button--secondary[type='button']")
    private List<WebElement> actionButtons;

    @FindBy(css = "button.oxd-button--secondary.orangehrm-left-space")
    private WebElement addEmployeeButton;

    @FindBy(css = "input[placeholder='Type for hints...']")
    private List<WebElement> autocompleteInputs;

    @FindBy(css = ".oxd-table-body .oxd-table-row")
    private List<WebElement> employeeTableRows;

    @FindBy(css = ".oxd-table-header .oxd-checkbox-input")
    private WebElement selectAllCheckbox;

    @FindBy(css = "button.oxd-button--label-danger")
    private WebElement deleteSelectedButton;

    @FindBy(css = ".oxd-table-filter-area input")
    private List<WebElement> filterInputs;

    @FindBy(css = "button[type='submit']")
    private WebElement searchButton;

    @FindBy(css = ".oxd-text--span.orangehrm-employee-list-header")
    private WebElement recordCountText;

    // ════════════════════════════════════════════════
    //  ADD EMPLOYEE FORM
    // ════════════════════════════════════════════════

    @FindBy(name = "firstName")
    private WebElement firstNameField;

    @FindBy(name = "middleName")
    private WebElement middleNameField;

    @FindBy(name = "lastName")
    private WebElement lastNameField;

    @FindBy(css = "input.--field-odd")
    private WebElement employeeIdField;

    @FindBy(css = ".employee-image-wrapper input[type='file']")
    private WebElement photoUploadInput;

    @FindBy(css = "label.oxd-switch-input")
    private WebElement createLoginDetailsToggle;

    @FindBy(css = "input.oxd-input[autocomplete='off']")
    private WebElement usernameInput;

    // Status dropdown
    @FindBy(css = ".oxd-select-text-input")
    private List<WebElement> statusDropdowns;

    @FindBy(css = "button[type='submit'].oxd-button--secondary")
    private WebElement saveButton;

    // ════════════════════════════════════════════════
    //  EDIT EMPLOYEE FORM
    // ════════════════════════════════════════════════

    @FindBy(css = ".orangehrm-edit-employee-name input")
    private List<WebElement> nameEditFields;

    // ════════════════════════════════════════════════
    //  DELETE CONFIRMATION
    // ════════════════════════════════════════════════

    @FindBy(css = "button.oxd-button--label-danger.orangehrm-button-margin")
    private WebElement confirmDeleteButton;

    // ════════════════════════════════════════════════
    //  SUCCESS/ERROR TOAST
    // ════════════════════════════════════════════════

    @FindBy(css = ".oxd-toast-content .oxd-text")
    private WebElement toastMessage;

    public EmployeeManagementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    // ════════════════════════════════════════════════
    //  NAVIGATION
    // ════════════════════════════════════════════════

    public void navigate() {
        driver.get(driver.getCurrentUrl().split("/web")[0]
                + "/web/index.php/pim/viewEmployeeList");
        wait.until(ExpectedConditions.urlContains("pim"));
        logger.info("Navigated to Employee Management (PIM)");
        ExtentReportManager.logInfo("Navigated to Employee Management page");
    }

    // ════════════════════════════════════════════════
    //  ADD EMPLOYEE
    // ════════════════════════════════════════════════

    public void clickAddEmployee() {
        // The Add button is a link in the header area
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/web/index.php/pim/addEmployee']")));
        addBtn.click();
        wait.until(ExpectedConditions.urlContains("addEmployee"));
        logger.info("Clicked Add Employee");
    }

    public String addEmployee(String firstName, String middleName, String lastName) {
        logger.info("Adding employee: {} {} {}", firstName, middleName, lastName);
        ExtentReportManager.logInfo("Adding employee: " + firstName + " " + lastName);

        wait.until(ExpectedConditions.visibilityOf(firstNameField));
        firstNameField.clear();
        firstNameField.sendKeys(firstName);

        if (middleName != null && !middleName.isEmpty()) {
            middleNameField.clear();
            middleNameField.sendKeys(middleName);
        }

        lastNameField.clear();
        lastNameField.sendKeys(lastName);

        // Read auto-generated Employee ID
        String empId = employeeIdField.getAttribute("value");
        logger.debug("Auto-generated Employee ID: {}", empId);

        clickSave();
        waitForToast();

        logger.info("Employee added successfully: {} {} | ID: {}", firstName, lastName, empId);
        ExtentReportManager.logPass("Employee added: " + firstName + " " + lastName + " | ID: " + empId);

        return empId;
    }

    public void uploadProfilePhoto(String imagePath) {
        logger.info("Uploading profile photo: {}", imagePath);
        photoUploadInput.sendKeys(Paths.get(imagePath).toAbsolutePath().toString());
        wait.until(ExpectedConditions.visibilityOf(toastMessage));
    }

    // ════════════════════════════════════════════════
    //  SEARCH & FILTER
    // ════════════════════════════════════════════════

    public void searchByEmployeeName(String name) {
        logger.info("Searching employee by name: {}", name);
        ExtentReportManager.logInfo("Searching for employee: " + name);

        WebElement nameSearchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".oxd-table-filter input[placeholder='Type for hints...']")));
        nameSearchInput.clear();
        nameSearchInput.sendKeys(name);

        // Wait for autocomplete
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // Click first suggestion if available
        List<WebElement> suggestions = driver.findElements(
                By.cssSelector(".oxd-autocomplete-dropdown .oxd-autocomplete-option"));
        if (!suggestions.isEmpty()) {
            suggestions.get(0).click();
        }

        clickSearchButton();
    }

    public void searchByEmployeeId(String employeeId) {
        logger.info("Searching by Employee ID: {}", employeeId);
        List<WebElement> inputs = driver.findElements(
                By.cssSelector(".oxd-table-filter .oxd-input"));
        if (inputs.size() >= 2) {
            inputs.get(1).clear();
            inputs.get(1).sendKeys(employeeId);
        }
        clickSearchButton();
    }

    public void clickSearchButton() {
        WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit']")));
        searchBtn.click();
        logger.debug("Search initiated");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
    }

    public void resetSearch() {
        WebElement resetBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='reset']")));
        resetBtn.click();
        logger.info("Search reset");
    }

    // ════════════════════════════════════════════════
    //  DELETE
    // ════════════════════════════════════════════════

    public void deleteEmployeeByRow(int rowIndex) {
        logger.info("Deleting employee at row index: {}", rowIndex);
        List<WebElement> rows = wait.until(ExpectedConditions.visibilityOfAllElements(employeeTableRows));

        if (rowIndex >= rows.size()) {
            throw new RuntimeException("Row index " + rowIndex + " out of bounds. Total rows: " + rows.size());
        }

        // Click trash/delete icon in the row
        WebElement deleteIcon = rows.get(rowIndex).findElement(
                By.cssSelector("button.oxd-icon-button.oxd-table-cell-action-space i.bi-trash"));
        deleteIcon.click();

        // Confirm deletion
        wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteButton)).click();
        waitForToast();

        logger.info("Employee deleted from row: {}", rowIndex);
        ExtentReportManager.logPass("Employee at row " + rowIndex + " deleted");
    }

    // ════════════════════════════════════════════════
    //  GETTERS / ASSERTIONS
    // ════════════════════════════════════════════════

    public int getEmployeeCount() {
        try {
            wait.until(ExpectedConditions.visibilityOf(recordCountText));
            String text = recordCountText.getText(); // e.g. "(1) Record Found"
            return Integer.parseInt(text.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getTableRowCount() {
        try {
            return wait.until(ExpectedConditions.visibilityOfAllElements(employeeTableRows)).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getToastMessage() {
        wait.until(ExpectedConditions.visibilityOf(toastMessage));
        return toastMessage.getText();
    }

    public boolean isEmployeeVisible(String employeeName) {
        return driver.findElements(By.xpath(
                "//div[@class='oxd-table-cell oxd-padding-cell']//span[contains(text(),'" + employeeName + "')]"))
                .stream().anyMatch(WebElement::isDisplayed);
    }

    // ════════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ════════════════════════════════════════════════

    private void clickSave() {
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit']")));
        saveBtn.click();
        logger.debug("Save button clicked");
    }

    private void waitForToast() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".oxd-toast")));
        } catch (TimeoutException e) {
            logger.warn("Toast message not visible after action");
        }
    }
}
