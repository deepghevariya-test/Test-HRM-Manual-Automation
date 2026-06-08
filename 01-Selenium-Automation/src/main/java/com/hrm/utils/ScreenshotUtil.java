package com.hrm.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * ScreenshotUtil - Captures and saves screenshots for test reporting.
 *
 * <p>Features:
 * <ul>
 *   <li>Failure screenshots auto-saved to screenshots/failed_tests/</li>
 *   <li>Returns Base64 for inline Extent Report embedding</li>
 *   <li>Timestamped filenames to prevent overwrite</li>
 * </ul>
 *
 * @author Deep Ghevariya
 */
public class ScreenshotUtil {

    private static final Logger logger = LogManager.getLogger(ScreenshotUtil.class);
    private static final String FAILURE_SCREENSHOT_DIR = "screenshots/failed_tests/";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() {}

    /**
     * Capture a screenshot on test failure and save to disk.
     * Also attaches to Extent Reports.
     *
     * @param driver   active WebDriver
     * @param testName name of the failed test
     * @return absolute path of saved screenshot file
     */
    public static String captureFailureScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            logger.warn("Cannot capture screenshot: WebDriver is null");
            return null;
        }

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String sanitizedTestName = testName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        String fileName = sanitizedTestName + "_" + timestamp + ".png";
        String filePath = FAILURE_SCREENSHOT_DIR + fileName;

        try {
            // Ensure directory exists
            Path dirPath = Paths.get(FAILURE_SCREENSHOT_DIR);
            Files.createDirectories(dirPath);

            // Take screenshot as bytes
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            // Save to disk
            Files.write(Paths.get(filePath), screenshotBytes);
            logger.info("Failure screenshot saved: {}", filePath);

            // Attach to Extent Report (Base64)
            String base64 = Base64.getEncoder().encodeToString(screenshotBytes);
            ExtentReportManager.attachScreenshot(base64, "FAILURE - " + testName);

            return new File(filePath).getAbsolutePath();

        } catch (IOException e) {
            logger.error("Failed to save screenshot for test: {}", testName, e);
            return null;
        }
    }

    /**
     * Capture screenshot and return as Base64 string (for embedding in reports).
     *
     * @param driver active WebDriver
     * @return Base64-encoded screenshot string, or null on failure
     */
    public static String captureAsBase64(WebDriver driver) {
        if (driver == null) return null;
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as Base64", e);
            return null;
        }
    }

    /**
     * Capture a named screenshot (e.g., for step-level evidence).
     *
     * @param driver          active WebDriver
     * @param screenshotName  descriptive name
     * @param subDir          subdirectory under screenshots/ (e.g., "evidence")
     * @return file path of saved screenshot
     */
    public static String captureScreenshot(WebDriver driver, String screenshotName, String subDir) {
        if (driver == null) return null;

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String dir = "screenshots/" + subDir + "/";
        String fileName = screenshotName + "_" + timestamp + ".png";
        String filePath = dir + fileName;

        try {
            Files.createDirectories(Paths.get(dir));
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(Paths.get(filePath), screenshotBytes);
            logger.debug("Screenshot saved: {}", filePath);
            return filePath;
        } catch (IOException e) {
            logger.error("Failed to capture screenshot: {}", screenshotName, e);
            return null;
        }
    }
}
