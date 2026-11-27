package com.qa.agent.tests;

import com.qa.agent.base.BaseTest;
import com.qa.agent.base.ConfigReader;
import com.qa.agent.pages.LoginPage;
import com.qa.agent.pages.ProductsPage;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Automated tests for the Login feature.
 * Source: outputs/login/test-cases.csv (TC_LOGIN_001 .. TC_LOGIN_023).
 * Severity mapping: High = CRITICAL, Medium = NORMAL, Low = MINOR.
 */
@Epic("Authentication")
@Feature("Login")
public class LoginTests extends BaseTest {

    private static final String USERNAME_REQUIRED = "Username is required";
    private static final String PASSWORD_REQUIRED = "Password is required";
    private static final String CREDENTIALS_DO_NOT_MATCH =
            "Username and password do not match any user in this service";
    private static final String LOCKED_OUT = "Sorry, this user has been locked out.";

    /** Matches the "error" CSS class as a whole word (not the base class "input_error"). */
    private static final Pattern ERROR_STATE_CLASS = Pattern.compile("(^|\\s)error(\\s|$)");
    private static final String ERROR_BORDER_RED = "rgb(226, 35, 26)";

    private LoginPage loginPage;
    private ProductsPage productsPage;

    // Runs after BaseTest.createContext(): TestNG runs superclass @BeforeMethod methods first.
    @BeforeMethod(alwaysRun = true)
    public void openLoginPage() {
        loginPage = new LoginPage(page);
        productsPage = new ProductsPage(page);
        Allure.step("Open the login page " + LoginPage.url(), () -> {
            loginPage.open();
        });
    }

    // =====================================================================
    // REQ-LOGIN-01: login page elements
    // =====================================================================

    @Test(description = "TC_LOGIN_001: Username field is displayed on the login page")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_LOGIN_001_usernameFieldIsDisplayed() {
        Allure.step("Verify the Username field is visible, enabled and has placeholder 'Username'", () -> {
            assertThat(loginPage.usernameInput()).isVisible();
            assertThat(loginPage.usernameInput()).isEnabled();
            assertThat(loginPage.usernameInput()).hasAttribute("placeholder", "Username");
        });
    }

    @Test(description = "TC_LOGIN_002: Password field is displayed on the login page")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_LOGIN_002_passwordFieldIsDisplayed() {
        Allure.step("Verify the Password field is visible, enabled and has placeholder 'Password'", () -> {
            assertThat(loginPage.passwordInput()).isVisible();
            assertThat(loginPage.passwordInput()).isEnabled();
            assertThat(loginPage.passwordInput()).hasAttribute("placeholder", "Password");
        });
    }

    @Test(description = "TC_LOGIN_003: Login button is displayed on the login page")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_LOGIN_003_loginButtonIsDisplayed() {
        Allure.step("Verify the Login button is visible, enabled and labelled 'Login'", () -> {
            assertThat(loginPage.loginButton()).isVisible();
            assertThat(loginPage.loginButton()).isEnabled();
            assertThat(loginPage.loginButton()).hasValue("Login");
        });
    }

    // =====================================================================
    // REQ-LOGIN-02: successful login
    // =====================================================================

    @Test(description = "TC_LOGIN_004: Valid user is redirected to the Products page URL")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_LOGIN_004_validUserIsRedirectedToProductsUrl() {
        Allure.step("Log in as the valid user", () ->
                loginPage.loginAs(ConfigReader.validUsername(), ConfigReader.password()));
        Allure.step("Verify the URL is " + ProductsPage.url(), () ->
                assertThat(page).hasURL(ProductsPage.url()));
    }

    @Test(description = "TC_LOGIN_024: Problem user is redirected to the Products page")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_LOGIN_024_problemUserIsRedirectedToProductsUrl() {
        Allure.step("Log in as problem_user", () ->
                loginPage.loginAs(ConfigReader.problemUsername(), ConfigReader.password()));
        Allure.step("Verify the URL is " + ProductsPage.url(), () ->
                assertThat(page).hasURL(ProductsPage.url()));
    }

    @Test(description = "TC_LOGIN_005: Valid user sees the Products page title")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_LOGIN_005_validUserSeesProductsTitle() {
        Allure.step("Log in as the valid user", () ->
                loginPage.loginAs(ConfigReader.validUsername(), ConfigReader.password()));
        Allure.step("Verify the page title shows 'Products'", () ->
                assertThat(productsPage.title()).hasText("Products"));
    }

    // =====================================================================
    // REQ-LOGIN-03 / REQ-LOGIN-04: empty field validation
    // =====================================================================

    @Test(description = "TC_LOGIN_006: Empty username shows 'Username is required'")
    @Severity(SeverityLevel.NORMAL)
    public void TC_LOGIN_006_emptyUsernameShowsUsernameRequired() {
        Allure.step("Leave Username empty, enter the password and click Login", () ->
                loginPage.loginAs("", ConfigReader.password()));
        Allure.step("Verify the error contains '" + USERNAME_REQUIRED + "'", () ->
                assertThat(loginPage.errorMessage()).containsText(USERNAME_REQUIRED));
        Allure.step("Verify the user stays on the login page", () ->
                assertThat(page).hasURL(LoginPage.url()));
    }

    @Test(description = "TC_LOGIN_007: Both fields empty shows only 'Username is required'")
    @Severity(SeverityLevel.NORMAL)
    public void TC_LOGIN_007_bothFieldsEmptyShowsOnlyUsernameRequired() {
        Allure.step("Leave both fields empty and click Login", () ->
                loginPage.clickLogin());
        Allure.step("Verify the error contains '" + USERNAME_REQUIRED + "' and not '" + PASSWORD_REQUIRED + "'", () -> {
            assertThat(loginPage.errorMessage()).containsText(USERNAME_REQUIRED);
            assertThat(loginPage.errorMessage()).not().containsText(PASSWORD_REQUIRED);
        });
        Allure.step("Verify the user stays on the login page", () ->
                assertThat(page).hasURL(LoginPage.url()));
    }

    @Test(description = "TC_LOGIN_008: Empty password shows 'Password is required'")
    @Severity(SeverityLevel.NORMAL)
    public void TC_LOGIN_008_emptyPasswordShowsPasswordRequired() {
        Allure.step("Enter the username, leave Password empty and click Login", () ->
                loginPage.loginAs(ConfigReader.validUsername(), ""));
        Allure.step("Verify the error contains '" + PASSWORD_REQUIRED + "'", () ->
                assertThat(loginPage.errorMessage()).containsText(PASSWORD_REQUIRED));
        Allure.step("Verify the user stays on the login page", () ->
                assertThat(page).hasURL(LoginPage.url()));
    }

    // =====================================================================
    // REQ-LOGIN-05: invalid credentials (data-driven)
    // =====================================================================

    /** High-priority invalid credential cases. Columns: test case ID, title, username, password. */
    @DataProvider(name = "invalidCredentialsHigh")
    public Object[][] invalidCredentialsHigh() {
        return new Object[][]{
                {"TC_LOGIN_009", "Valid username with wrong password is rejected", ConfigReader.validUsername(), "wrong_pass"},
                {"TC_LOGIN_010", "Unknown username with valid password is rejected", "unknown_user", ConfigReader.password()},
                {"TC_LOGIN_011", "Unknown username with wrong password is rejected", "unknown_user", "wrong_pass"},
        };
    }

    /** Medium-priority boundary and security cases. Columns: test case ID, title, username, password. */
    @DataProvider(name = "invalidCredentialsMedium")
    public Object[][] invalidCredentialsMedium() {
        return new Object[][]{
                {"TC_LOGIN_012", "Username with different letter case is rejected", "Standard_User", ConfigReader.password()},
                {"TC_LOGIN_013", "Username with leading and trailing spaces is rejected",
                        " " + ConfigReader.validUsername() + " ", ConfigReader.password()},
                {"TC_LOGIN_022", "SQL injection string does not log the user in", "' OR '1'='1", "' OR '1'='1"},
        };
    }

    @Test(dataProvider = "invalidCredentialsHigh",
            description = "TC_LOGIN_009 / TC_LOGIN_010 / TC_LOGIN_011: Invalid credentials are rejected")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_LOGIN_009_010_011_invalidCredentialsAreRejected(String tcId, String title,
                                                                   String username, String password) {
        verifyCredentialsRejected(tcId, title, username, password);
    }

    @Test(dataProvider = "invalidCredentialsMedium",
            description = "TC_LOGIN_012 / TC_LOGIN_013 / TC_LOGIN_022: Case, whitespace and SQL injection inputs are rejected")
    @Severity(SeverityLevel.NORMAL)
    public void TC_LOGIN_012_013_022_edgeCaseCredentialsAreRejected(String tcId, String title,
                                                                    String username, String password) {
        verifyCredentialsRejected(tcId, title, username, password);
    }

    private void verifyCredentialsRejected(String tcId, String title, String username, String password) {
        Allure.getLifecycle().updateTestCase(result -> result.setName(tcId + ": " + title));
        Allure.parameter("Test case ID", tcId);
        Allure.step("Log in with username '" + username + "' and password '" + password + "'", () ->
                loginPage.loginAs(username, password));
        Allure.step("Verify the error contains '" + CREDENTIALS_DO_NOT_MATCH + "'", () ->
                assertThat(loginPage.errorMessage()).containsText(CREDENTIALS_DO_NOT_MATCH));
        Allure.step("Verify the user stays on the login page", () ->
                assertThat(page).hasURL(LoginPage.url()));
    }

    @Test(description = "TC_LOGIN_023: Script in the username field is not executed")
    @Severity(SeverityLevel.NORMAL)
    public void TC_LOGIN_023_scriptInUsernameIsNotExecuted() {
        AtomicBoolean dialogShown = new AtomicBoolean(false);
        page.onDialog(dialog -> {
            dialogShown.set(true);
            dialog.dismiss();
        });
        Allure.step("Log in with a script string as username", () ->
                loginPage.loginAs("<script>alert(1)</script>", "x"));
        Allure.step("Verify the error contains '" + CREDENTIALS_DO_NOT_MATCH + "'", () ->
                assertThat(loginPage.errorMessage()).containsText(CREDENTIALS_DO_NOT_MATCH));
        Allure.step("Verify the user stays on the login page", () ->
                assertThat(page).hasURL(LoginPage.url()));
        Allure.step("Verify no browser alert or dialog was shown", () ->
                Assert.assertFalse(dialogShown.get(), "A browser dialog appeared: the script was executed"));
    }

    // =====================================================================
    // REQ-LOGIN-06: locked-out user
    // =====================================================================

    @Test(description = "TC_LOGIN_014: Locked-out user stays on the login page")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_LOGIN_014_lockedOutUserStaysOnLoginPage() {
        Allure.step("Log in as the locked-out user", () ->
                loginPage.loginAs(ConfigReader.lockedUsername(), ConfigReader.password()));
        Allure.step("Verify the error is shown, so the login attempt has been processed", () ->
                assertThat(loginPage.errorMessage()).isVisible());
        Allure.step("Verify the URL is still the login page and the Products page is not shown", () -> {
            assertThat(page).hasURL(LoginPage.url());
            assertThat(productsPage.title()).not().isVisible();
        });
    }

    @Test(description = "TC_LOGIN_015: Locked-out user sees the locked-out message")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_LOGIN_015_lockedOutUserSeesLockedMessage() {
        Allure.step("Log in as the locked-out user", () ->
                loginPage.loginAs(ConfigReader.lockedUsername(), ConfigReader.password()));
        Allure.step("Verify the error contains '" + LOCKED_OUT + "'", () ->
                assertThat(loginPage.errorMessage()).containsText(LOCKED_OUT));
    }

    // =====================================================================
    // REQ-LOGIN-07: closing the error
    // =====================================================================

    @Test(description = "TC_LOGIN_016: Closing the error removes the error message")
    @Severity(SeverityLevel.MINOR)
    public void TC_LOGIN_016_closingErrorRemovesMessage() {
        Allure.step("Leave both fields empty, click Login and check the error appears", () -> {
            loginPage.clickLogin();
            assertThat(loginPage.errorMessage()).isVisible();
        });
        Allure.step("Click the X button on the error message", () -> loginPage.closeError());
        Allure.step("Verify the error message is no longer visible", () ->
                assertThat(loginPage.errorMessage()).not().isVisible());
    }

    @Test(description = "TC_LOGIN_017: Closing the error removes the error icons from the fields")
    @Severity(SeverityLevel.MINOR)
    public void TC_LOGIN_017_closingErrorRemovesFieldIcons() {
        Allure.step("Leave both fields empty, click Login and check an error icon appears in each field", () -> {
            loginPage.clickLogin();
            assertThat(loginPage.fieldErrorIcons()).hasCount(2);
        });
        Allure.step("Click the X button on the error message", () -> loginPage.closeError());
        Allure.step("Verify no error icon remains in the Username or Password field", () ->
                assertThat(loginPage.fieldErrorIcons()).hasCount(0));
    }

    @Test(description = "TC_LOGIN_018: Closing the error removes the red borders from the fields")
    @Severity(SeverityLevel.MINOR)
    public void TC_LOGIN_018_closingErrorRemovesRedBorders() {
        Allure.step("Leave both fields empty, click Login and check both fields have the red error border", () -> {
            loginPage.clickLogin();
            assertThat(loginPage.usernameInput()).hasClass(ERROR_STATE_CLASS);
            assertThat(loginPage.passwordInput()).hasClass(ERROR_STATE_CLASS);
            assertThat(loginPage.usernameInput()).hasCSS("border-bottom-color", ERROR_BORDER_RED);
            assertThat(loginPage.passwordInput()).hasCSS("border-bottom-color", ERROR_BORDER_RED);
        });
        Allure.step("Click the X button on the error message", () -> loginPage.closeError());
        Allure.step("Verify neither field has the error class or the red border", () -> {
            assertThat(loginPage.usernameInput()).not().hasClass(ERROR_STATE_CLASS);
            assertThat(loginPage.passwordInput()).not().hasClass(ERROR_STATE_CLASS);
            assertThat(loginPage.usernameInput()).not().hasCSS("border-bottom-color", ERROR_BORDER_RED);
            assertThat(loginPage.passwordInput()).not().hasCSS("border-bottom-color", ERROR_BORDER_RED);
        });
    }

    // =====================================================================
    // REQ-LOGIN-08: password masking
    // =====================================================================

    @Test(description = "TC_LOGIN_019: Password field hides the typed characters")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_LOGIN_019_passwordFieldMasksInput() {
        Allure.step("Type the password into the Password field", () -> {
            loginPage.enterPassword(ConfigReader.password());
        });
        Allure.step("Verify the Password input has type='password' (characters are masked)", () ->
                assertThat(loginPage.passwordInput()).hasAttribute("type", "password"));
    }

    // =====================================================================
    // REQ-LOGIN-09: keyboard login (PO Q7)
    // =====================================================================

    @Test(description = "TC_LOGIN_020: Valid user can log in by pressing Enter")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_LOGIN_020_validUserCanLoginWithEnter() {
        Allure.step("Enter the username and password", () -> {
            loginPage.enterUsername(ConfigReader.validUsername());
            loginPage.enterPassword(ConfigReader.password());
        });
        Allure.step("Press Enter in the Password field (no click on Login)", () ->
                loginPage.submitWithEnterFromPassword());
        Allure.step("Verify the Products page is shown", () -> {
            assertThat(page).hasURL(ProductsPage.url());
            assertThat(productsPage.title()).hasText("Products");
        });
    }

    @Test(description = "TC_LOGIN_021: Valid user can log in using only the keyboard")
    @Severity(SeverityLevel.NORMAL)
    public void TC_LOGIN_021_validUserCanLoginWithKeyboardOnly() {
        Allure.step("Click the Username field and type the username", () -> {
            loginPage.clickUsernameField();
            loginPage.typeWithKeyboard(ConfigReader.validUsername());
        });
        Allure.step("Press Tab and verify focus moves to the Password field", () -> {
            loginPage.pressTab();
            assertThat(loginPage.passwordInput()).isFocused();
        });
        Allure.step("Type the password", () -> loginPage.typeWithKeyboard(ConfigReader.password()));
        Allure.step("Press Tab and verify focus moves to the Login button", () -> {
            loginPage.pressTab();
            assertThat(loginPage.loginButton()).isFocused();
        });
        Allure.step("Press Enter", () -> loginPage.pressEnter());
        Allure.step("Verify the URL is " + ProductsPage.url(), () ->
                assertThat(page).hasURL(ProductsPage.url()));
    }

}
