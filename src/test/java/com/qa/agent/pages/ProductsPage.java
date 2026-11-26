package com.qa.agent.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.qa.agent.base.ConfigReader;

/**
 * Page Object for the Products (inventory) page shown after a successful login.
 * Locators were verified on the live page with Playwright MCP. No assertions here.
 */
public class ProductsPage {

    private final Locator title;

    public ProductsPage(Page page) {
        this.title = page.locator("[data-test='title']");
    }

    /** URL of the Products page, e.g. https://www.saucedemo.com/inventory.html */
    public static String url() {
        return ConfigReader.baseUrl() + "/inventory.html";
    }

    public Locator title() {
        return title;
    }
}
