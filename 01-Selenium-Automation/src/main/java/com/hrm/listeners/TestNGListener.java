package com.hrm.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.hrm.base.BaseTest;
import com.hrm.utils.ExtentReportManager;
import com.hrm.utils.ScreenshotUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;

/**
 * TestNGListener - Hooks into TestNG lifecycle to enrich Extent Reports.
 *
 * <p>Registered via testng.xml or @Listeners annotation on test classes.
 * Handles:
 * <ul>
 *   <li>Test start/pass/fail/skip logging</li>
 *   <li>Automatic failure screenshot attachment</li>
 *   <li>Exception detail capture in reports</li>
 *   <li>Test suite start/finish console summary</li>
 * </ul>
 *
 * @author Deep Ghevariya
 */
public class TestNGListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestNGListener.class);

    // ════════════════════════════════════════════════
    //  SUITE LEVEL
    // ════════════════════════════════════════════════

    @Override
    public void onStart(ITestContext context) {
        logger.info("╔══════════════════════════════════════════╗");
        logger.info("║  TEST SUITE STARTED: {}  ║", padRight(context.getName(), 22));
        logger.info("╚══════════════════════════════════════════╝");
        logger.info("Total Tests Planned: {}", context.getAllTestMethods().length);
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("╔══════════════════════════════════════════╗");
        logger.info("║  TEST SUITE FINISHED: {}  ║", padRight(context.getName(), 21));
        logger.info("╠══════════════════════════════════════════╣");
        logger.info("║  ✅ Passed  : {}  ║", padRight(String.valueOf(context.getPassedTests().size()), 26));
        logger.info("║  ❌ Failed  : {}  ║", padRight(String.valueOf(context.getFailedTests().size()), 26));
        logger.info("║  ⏭️  Skipped : {}  ║", padRight(String.valueOf(context.getSkippedTests().size()), 26));
        logger.info("╚══════════════════════════════════════════╝");
        ExtentReportManager.flushReport();
    }

    // ════════════════════════════════════════════════
    //  TEST LEVEL
    // ════════════════════════════════════════════════

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        String[] groups = result.getMethod().getGroups();

        logger.info("▶ Starting test: {} | Groups: {}", testName, Arrays.toString(groups));

        // Create Extent test node
        ExtentTest test = ExtentReportManager.createTest(
                testName,
                description != null ? description : "Test: " + testName,
                groups
        );
        test.info("Test started | Parameters: " + Arrays.toString(result.getParameters()));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("✅ PASSED: {}", result.getMethod().getMethodName());
        ExtentReportManager.logPass("Test PASSED ✅");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();

        logger.error("❌ FAILED: {} | Error: {}", testName,
                throwable != null ? throwable.getMessage() : "Unknown error");

        ExtentReportManager.logFail("Test FAILED ❌ | " + (throwable != null ? throwable.getMessage() : ""));

        // Capture and attach screenshot
        if (BaseTest.getDriver() != null) {
            String base64 = ScreenshotUtil.captureAsBase64(BaseTest.getDriver());
            if (base64 != null) {
                ExtentReportManager.attachScreenshot(base64, "Failure Screenshot - " + testName);
            }
        }

        // Log stack trace to report
        if (throwable != null) {
            ExtentReportManager.getTest().fail(throwable);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.warn("⏭️ SKIPPED: {} | Reason: {}", testName,
                result.getThrowable() != null ? result.getThrowable().getMessage() : "N/A");
        ExtentReportManager.logSkip("Test SKIPPED ⏭️ | "
                + (result.getThrowable() != null ? result.getThrowable().getMessage() : "No reason provided"));
    }

    // ════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
