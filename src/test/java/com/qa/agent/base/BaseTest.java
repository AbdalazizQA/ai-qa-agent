package com.qa.agent.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;

/**
 * Browser lifecycle for all UI tests.
 * <ul>
 *   <li>One Playwright + Browser per test class.</li>
 *   <li>A fresh BrowserContext + Page per test method, so tests are independent.</li>
 *   <li>Headless by default; run with {@code -Dheadless=false} to watch.</li>
 *   <li>On failure a full-page screenshot is attached to the Allure report.</li>
 * </ul>
 */
public abstract class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeClass(alwaysRun = true)
    public void launchBrowser() {
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
    }

    @BeforeMethod(alwaysRun = true)
    public void createContext() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterMethod(alwaysRun = true)
    public void closeContext(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE && page != null && !page.isClosed()) {
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                Allure.addAttachment("Screenshot on failure - " + result.getMethod().getMethodName(),
                        "image/png", new ByteArrayInputStream(screenshot), "png");
            }
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
