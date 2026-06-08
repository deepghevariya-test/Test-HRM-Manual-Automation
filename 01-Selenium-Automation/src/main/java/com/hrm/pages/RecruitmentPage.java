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
 * RecruitmentPage - Page Object for OrangeHRM Recruitment module.
 *
 * <p>Covers: Job postings, candidate applications, interview scheduling, offer letters.
 *
 * @author Deep Ghevariya
 */
public class RecruitmentPage {

    private static final Logger logger = LogManager.getLogger(RecruitmentPage.class);
    private final WebDriver driver;
    private final WebDriverWait wait;

    // ════════════════════════════════════════════════
    //  VACANCY LIST
    // ════════════════════════════════════════════════

    @FindBy(css = ".oxd-table-body .oxd-table-row")
    private List<WebElement> vacancyRows;

    @FindBy(css = "a[href*='addJobVacancy']")
    private WebElement addVacancyLink;

    // ════════════════════════════════════════════════
    //  ADD VACANCY FORM
    // ════════════════════════════════════════════════

    @FindBy(css = ".oxd-input:not([type='hidden'])")
    private List<WebElement> textInputs;

    @FindBy(css = ".oxd-select-text-input")
    private List<WebElement> dropdowns;

    @FindBy(css = ".oxd-textarea")
    private WebElement descriptionField;

    @FindBy(css = "button[type='submit']")
    private WebElement saveButton;

    // ════════════════════════════════════════════════
    //  CANDIDATE LIST
    // ════════════════════════════════════════════════

    @FindBy(css = "a[href*='addCandidate']")
    private WebElement addCandidateLink;

    @FindBy(css = ".oxd-table-body .oxd-table-row")
    private List<WebElement> candidateRows;

    // ════════════════════════════════════════════════
    //  TOAST
    // ════════════════════════════════════════════════

    @FindBy(css = ".oxd-toast-content-text")
    private WebElement toastMessage;

    public RecruitmentPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public void navigateToVacancies() {
        driver.get(driver.getCurrentUrl().split("/web")[0]
                + "/web/index.php/recruitment/viewJobVacancy");
        wait.until(ExpectedConditions.urlContains("viewJobVacancy"));
        logger.info("Navigated to Job Vacancies");
    }

    public void navigateToCandidates() {
        driver.get(driver.getCurrentUrl().split("/web")[0]
                + "/web/index.php/recruitment/viewCandidates");
        wait.until(ExpectedConditions.urlContains("viewCandidates"));
        logger.info("Navigated to Candidates");
    }

    public void clickAddVacancy() {
        wait.until(ExpectedConditions.elementToBeClickable(addVacancyLink)).click();
        wait.until(ExpectedConditions.urlContains("addJobVacancy"));
        logger.info("Clicked Add Vacancy");
    }

    public void fillVacancyForm(String vacancyName, String hiringManagerName, String description) {
        logger.info("Filling vacancy: {}", vacancyName);

        // Vacancy name (first text input)
        wait.until(ExpectedConditions.visibilityOf(textInputs.get(0)));
        textInputs.get(0).clear();
        textInputs.get(0).sendKeys(vacancyName);

        // Description
        if (descriptionField != null && description != null) {
            descriptionField.sendKeys(description);
        }

        // Hiring manager (autocomplete)
        WebElement managerInput = driver.findElement(
                By.cssSelector("input[placeholder='Type for hints...']"));
        managerInput.sendKeys(hiringManagerName);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        List<WebElement> opts = driver.findElements(
                By.cssSelector(".oxd-autocomplete-option span"));
        if (!opts.isEmpty()) opts.get(0).click();
    }

    public void saveVacancy() {
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
        wait.until(ExpectedConditions.visibilityOf(toastMessage));
        logger.info("Vacancy saved");
    }

    public int getVacancyCount() {
        return vacancyRows.size();
    }

    public int getCandidateCount() {
        return candidateRows.size();
    }

    public String getToastMessage() {
        wait.until(ExpectedConditions.visibilityOf(toastMessage));
        return toastMessage.getText();
    }
}
