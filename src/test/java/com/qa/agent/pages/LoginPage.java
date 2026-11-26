package com.qa.agent.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.qa.agent.base.ConfigReader;

/**
 * Page Object for the Swag Labs login page (https://www.saucedemo.com/).
 * Locators were verified on the live page with Playwright MCP. No assertions here.
 */
public class LoginPage {

    private final Page page;

    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;
    private final Locator errorCloseButton;
    private final Locator fieldErrorIcons;

    public LoginPage(Page page) {
        this.page = page;
        this.usernameInput = page.locator("[data-test='username']");
        this.passwordInput = page.locator("[data-test='password']");
        this.loginButton = page.locator("[data-test='login-button']");
        this.errorMessage = page.locator("[data-test='error']");
        this.errorCloseButton = page.locator("[data-test='error-button']");
        // The red "x" icons inside the fields have no data-test attribute or accessible role
        // (aria-hidden svg), so a CSS class is the only stable locator.
        this.fieldErrorIcons = page.locator(".form_group .error_icon");
    }

    // ---------- navigation ----------

    /** URL of the login page, e.g. https://www.saucedemo.com/ */
    public static String url() {
        return ConfigReader.baseUrl() + "/";
    }

    public LoginPage open() {
        page.navigate(url());
        return this;
    }

    // ---------- actions ----------

    public LoginPage enterUsername(String username) {
        usernameInput.fill(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        passwordInput.fill(password);
        return this;
    }

    public void clickLogin() {
        loginButton.click();
    }

    public void loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    /** Presses Enter while the cursor is in the Password field (no click on Login). */
    public void submitWithEnterFromPassword() {
        passwordInput.press("Enter");
    }

    public void clickUsernameField() {
        usernameInput.click();
    }

    public void typeWithKeyboard(String text) {
        page.keyboard().type(text);
    }

    public void pressTab() {
        page.keyboard().press("Tab");
    }

    public void pressEnter() {
        page.keyboard().press("Enter");
    }

    public void closeError() {
        errorCloseButton.click();
    }

    // ---------- element accessors (for assertions in tests) ----------

    public Locator usernameInput() {
        return usernameInput;
    }

    public Locator passwordInput() {
        return passwordInput;
    }

    public Locator loginButton() {
        return loginButton;
    }

    public Locator errorMessage() {
        return errorMessage;
    }

    public Locator fieldErrorIcons() {
        return fieldErrorIcons;
    }
}
