package com.hrm.base;

import com.hrm.utils.ConfigReader;
import com.hrm.utils.ExtentReportManager;
import com.hrm.utils.ScreenshotUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.time.Duration;

/**
 * BaseTest - Core foundation for all Selenium test classes.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Cross-browser WebDriver initialization (Chrome / Firefox / Edge)</li>
 *   <li>Headless execution support</li>
 *   <li>Implicit wait + page load timeout configuration</li>
 *   <li>Extent Reports setup and teardown</li>
 *   <li>Auto-screenshot capture on test failure</li>
 *   <li>ThreadLocal WebDriver for parallel test safety</li>
 * </ul>
 *
 * @author Deep Ghevariya
 * @version 1.0
 */
public class BaseTest {

    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    /** Thread-safe WebDriver for parallel test execution */
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    protected static final String BASE_URL = ConfigReader.get("base.url");
    protected static final int IMPLICIT_WAIT = Integer.parseInt(ConfigReader.get("implicit.wait.sec", "10"));
    protected static final int PAGE_LOAD_TIMEOUT = Integer.parseInt(ConfigReader.get("page.load.timeout.sec", "30"));

    // ════════════════════════════════════════════════
    //  DRIVER ACCESSOR
    // ════════════════════════════════════════════════

    /**
     * Returns the WebDriver for the current thread.
     * Safe for parallel test execution.
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    // ════════════════════════════════════════════════
    //  SETUP & TEARDOWN
    // ════════════════════════════════════════════════

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        logger.info("═══════════════════════════════════════════");
        logger.info(" HRM Test Suite Starting");
        logger.info(" Target: {}", BASE_URL);
        logger.info("═══════════════════════════════════════════");
        ExtentReportManager.initReport();
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional("") String browserParam) {
        String browser = resolveBrowser(browserParam);
        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", ConfigReader.get("headless", "false"))
        );

        logger.info("Initializing WebDriver → browser={}, headless={}", browser, headless);
        WebDriver driver = createDriver(browser, headless);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_TIMEOUT));
        driver.manage().window().maximize();

        driverThreadLocal.set(driver);
        logger.info("WebDriver initialized successfully | Browser: {}", browser.toUpperCase());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        WebDriver driver = getDriver();
        if (driver == null) return;

        try {
            // Capture screenshot on failure
            if (result.getStatus() == ITestResult.FAILURE) {
                logger.warn("Test FAILED: {} — capturing screenshot", result.getName());
                String screenshotPath = ScreenshotUtil.captureFailureScreenshot(driver, result.getName());
                logger.info("Screenshot saved: {}", screenshotPath);
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                logger.info("Test PASSED: {}", result.getName());
            } else {
                logger.warn("Test SKIPPED: {}", result.getName());
            }
        } finally {
            driver.quit();
            driverThreadLocal.remove();
            logger.debug("WebDriver closed and removed from ThreadLocal");
        }
    }

    @AfterSuite(alwaysRun = true)
    public void globalTearDown() {
        ExtentReportManager.flushReport();
        logger.info("═══════════════════════════════════════════");
        logger.info(" HRM Test Suite Completed");
        logger.info(" Extent Report: reports/ExtentReport.html");
        logger.info("═══════════════════════════════════════════");
    }

    // ════════════════════════════════════════════════
    //  WEBDRIVER FACTORY
    // ════════════════════════════════════════════════

    /**
     * Factory method — creates WebDriver instance for the given browser.
     *
     * @param browser  "chrome", "firefox", or "edge"
     * @param headless true for headless execution (CI/CD pipelines)
     * @return configured WebDriver instance
     */
    private WebDriver createDriver(String browser, boolean headless) {
        return switch (browser.toLowerCase()) {
            case "firefox" -> createFirefoxDriver(headless);
            case "edge"    -> createEdgeDriver(headless);
            default        -> createChromeDriver(headless);
        };
    }

    private WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        logger.debug("ChromeDriver created | headless={}", headless);
        return new ChromeDriver(options);
    }

    private WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("--headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }
        logger.debug("FirefoxDriver created | headless={}", headless);
        return new FirefoxDriver(options);
    }

    private WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        options.addArguments("--disable-notifications");
        logger.debug("EdgeDriver created | headless={}", headless);
        return new EdgeDriver(options);
    }

    /**
     * Resolves browser from system property → TestNG parameter → config → default.
     */
    private String resolveBrowser(String paramBrowser) {
        String sysProp = System.getProperty("browser");
        if (sysProp != null && !sysProp.isEmpty()) return sysProp;
        if (paramBrowser != null && !paramBrowser.isEmpty()) return paramBrowser;
        String configBrowser = ConfigReader.get("browser", "chrome");
        return (configBrowser != null && !configBrowser.isEmpty()) ? configBrowser : "chrome";
    }

    // ════════════════════════════════════════════════
    //  HELPER METHODS FOR SUBCLASSES
    // ════════════════════════════════════════════════

    /** Navigate to the application base URL */
    protected void navigateToBaseUrl() {
        getDriver().get(BASE_URL);
        logger.info("Navigated to base URL: {}", BASE_URL);
    }

    /** Navigate to a specific path relative to base URL */
    protected void navigateTo(String path) {
        String url = BASE_URL + path;
        getDriver().get(url);
        logger.info("Navigated to: {}", url);
    }
}
