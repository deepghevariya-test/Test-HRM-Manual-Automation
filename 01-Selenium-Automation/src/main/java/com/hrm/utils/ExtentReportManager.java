package com.hrm.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ExtentReportManager - Manages Extent Reports 5 lifecycle.
 *
 * <p>Features:
 * <ul>
 *   <li>Thread-safe ExtentTest for parallel execution</li>
 *   <li>Auto-timestamped report filename</li>
 *   <li>Dark theme with HRM project branding</li>
 *   <li>Category tagging support</li>
 * </ul>
 *
 * @author Deep Ghevariya
 */
public class ExtentReportManager {

    private static final Logger logger = LogManager.getLogger(ExtentReportManager.class);

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    private static final String REPORT_DIR    = "reports/";
    private static final String REPORT_NAME   = "HRM_Automation_Report";
    private static final String PROJECT_NAME  = "HRM Testing Automation Framework";
    private static final String TESTER_NAME   = "Deep Ghevariya";

    private ExtentReportManager() {}

    // ════════════════════════════════════════════════
    //  INITIALIZATION
    // ════════════════════════════════════════════════

    /** Initialize ExtentReports with Spark reporter. Call once per suite (@BeforeSuite). */
    public static synchronized void initReport() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String reportPath = Paths.get(REPORT_DIR, REPORT_NAME + "_" + timestamp + ".html")
                .toString();

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        configureReporter(sparkReporter);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // System info shown on report dashboard
        extent.setSystemInfo("QA Engineer", TESTER_NAME);
        extent.setSystemInfo("Project", PROJECT_NAME);
        extent.setSystemInfo("Environment", ConfigReader.get("environment", "QA"));
        extent.setSystemInfo("Base URL", ConfigReader.get("base.url", "N/A"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));

        logger.info("ExtentReports initialized → {}", reportPath);
    }

    private static void configureReporter(ExtentSparkReporter reporter) {
        reporter.config().setTheme(Theme.DARK);
        reporter.config().setDocumentTitle(PROJECT_NAME + " - Test Report");
        reporter.config().setReportName(PROJECT_NAME);
        reporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        reporter.config().setEncoding("UTF-8");
    }

    // ════════════════════════════════════════════════
    //  TEST MANAGEMENT
    // ════════════════════════════════════════════════

    /** Create a new ExtentTest for the current thread. */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = extent.createTest(testName, description);
        testThreadLocal.set(test);
        return test;
    }

    /** Create a test with category tags (e.g., "Smoke", "Regression"). */
    public static ExtentTest createTest(String testName, String description, String... categories) {
        ExtentTest test = createTest(testName, description);
        for (String category : categories) {
            test.assignCategory(category);
        }
        test.assignAuthor(TESTER_NAME);
        return test;
    }

    /** Get current thread's ExtentTest instance. */
    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    // ════════════════════════════════════════════════
    //  LOGGING HELPERS
    // ════════════════════════════════════════════════

    public static void logInfo(String message) {
        if (getTest() != null) getTest().log(Status.INFO, message);
        logger.info(message);
    }

    public static void logPass(String message) {
        if (getTest() != null) getTest().log(Status.PASS, message);
        logger.info("[PASS] {}", message);
    }

    public static void logFail(String message) {
        if (getTest() != null) getTest().log(Status.FAIL, message);
        logger.error("[FAIL] {}", message);
    }

    public static void logWarning(String message) {
        if (getTest() != null) getTest().log(Status.WARNING, message);
        logger.warn("[WARN] {}", message);
    }

    public static void logSkip(String message) {
        if (getTest() != null) getTest().log(Status.SKIP, message);
        logger.info("[SKIP] {}", message);
    }

    /** Attach base64-encoded screenshot to the report. */
    public static void attachScreenshot(String base64Screenshot, String title) {
        if (getTest() != null) {
            getTest().addScreenCaptureFromBase64String(base64Screenshot, title);
        }
    }

    // ════════════════════════════════════════════════
    //  FLUSH
    // ════════════════════════════════════════════════

    /** Flush and close the report. Call once per suite (@AfterSuite). */
    public static synchronized void flushReport() {
        if (extent != null) {
            extent.flush();
            logger.info("ExtentReports flushed. Report generation complete.");
        }
        testThreadLocal.remove();
    }
}
