package com.hrm.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * RetryAnalyzer - Retries flaky tests up to MAX_RETRY_COUNT times.
 *
 * <p>Usage: @Test(retryAnalyzer = RetryAnalyzer.class)
 * Or configure globally via TestNGListener / surefire plugin.
 *
 * @author Deep Ghevariya
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRY_COUNT = 2;  // retry up to 2 times (3 total attempts)

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            logger.warn("⟳ Retrying test '{}' — Attempt {}/{}",
                    result.getName(), retryCount, MAX_RETRY_COUNT);
            return true;
        }
        logger.error("✗ Test '{}' failed after {} retries", result.getName(), MAX_RETRY_COUNT);
        return false;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public static int getMaxRetryCount() {
        return MAX_RETRY_COUNT;
    }
}
