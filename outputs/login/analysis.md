# QA Analysis: Login

- **Requirement:** `requirements/login.md`
- **Application under test:** Swag Labs: https://www.saucedemo.com
- **Analysis date:** 2026-11-27
- **Method:** I read the requirement and then checked the live app with Playwright MCP (Chromium).

---

## 1. Summary

The Login feature lets a registered customer sign in to Swag Labs with a username and password so they can browse products and place orders. A user with valid credentials goes to the Products page. Invalid, incomplete or locked-out logins are blocked and the user sees an error message they can close.

---

## 2. Testable Requirements

| ID | Requirement (source: Acceptance Criterion) | Priority |
|----|--------------------------------------------|----------|
| REQ-LOGIN-01 | The login page shows a username field, a password field and a Login button. (AC1) | High |
| REQ-LOGIN-02 | A user with valid credentials (`standard_user` / `secret_sauce`) is redirected to the Products page after clicking Login. (AC2) | High |
| REQ-LOGIN-03 | If the username is empty, the error message "Username is required" is shown. (AC3) | Medium |
| REQ-LOGIN-04 | If the password is empty, the error message "Password is required" is shown. (AC4) | Medium |
| REQ-LOGIN-05 | If the username or password is wrong, an error message says the credentials do not match. (AC5) | High |
| REQ-LOGIN-06 | A locked-out user (`locked_out_user`) cannot log in and sees a message that the account is locked. (AC6) | High |
| REQ-LOGIN-07 | The user can close the error message. (AC7) | Low |
| REQ-LOGIN-08 | The password field hides the characters typed. (AC8) | High |

---

## 3. Ambiguities & Open Questions

Questions for the Product Owner:

1. **Exact error text:** AC3 and AC4 give exact strings, but the app adds the prefix "Epic sadface: ". Is the prefix part of the expected message, or should tests check only that the message contains the stated text?
2. **AC5 wording:** AC5 gives no exact text, only "telling the user that the credentials do not match". Is the observed text "Username and password do not match any user in this service" the agreed wording?
3. **AC6 wording:** AC6 gives no exact text either. Is "Sorry, this user has been locked out." acceptable for "a message saying the account is locked"?
4. **Both fields empty:** When both fields are empty, which error should show? The app shows only "Username is required". Is that the intended priority?
5. **Case sensitivity:** Should usernames be case-sensitive? The app rejects `Standard_User`.
6. **Whitespace:** Should leading or trailing spaces in the username be trimmed? The app rejects `" standard_user "`.
7. **Submitting with Enter:** AC2 says "after clicking Login". Is pressing Enter in a field an equally valid way to submit? It works in the app.
8. **Field state after an error:** After a failed login, should the fields be cleared or keep their values? The app keeps both, including the typed password.
9. **Error highlighting:** When an error appears, the app marks both fields with red borders and error icons, even if only one field is wrong. Is that intended? Is the highlighting part of the requirement?
10. **Closing the error (AC7):** Closing the error removes the message and the icons, but the red border class (`input_error`) stays on the fields. Should closing the error reset the whole error state?
11. **Brute force:** Should the account lock after several failed attempts, or should attempts be rate-limited? Nothing is stated.
12. **Credentials on the page:** The login page lists every accepted username and the shared password. Is that only acceptable because this is a demo or test environment, and is it out of scope?
13. **Other demo users:** The page lists four more accounts (`problem_user`, `performance_glitch_user`, `error_user`, `visual_user`). Are their logins in scope for this feature?
14. **Session behavior:** Is session handling (persistence, logout, direct URL access while logged out) part of this feature or a separate feature?
15. **Limits:** Is there a maximum length or a character restriction for the username and password? Nothing is stated, and the fields have no `maxlength`.
16. **Accessibility and browsers:** Are there accessibility (WCAG level) or browser and device support requirements for the login page?

---

## 4. Requirement vs Actual Behavior

| Req | Requirement says | Observed in app | Assessment |
|-----|------------------|-----------------|------------|
| REQ-LOGIN-01 | Username field, password field, Login button | Present: `data-test="username"`, `data-test="password"`, `data-test="login-button"` (value "Login") | Matches |
| REQ-LOGIN-02 | Redirected to Products page | Goes to `/inventory.html`, and the page title element shows "Products" | Matches |
| REQ-LOGIN-03 | "Username is required" | "Epic sadface: Username is required" | **Difference:** the app adds the prefix "Epic sadface: " (see Q1) |
| REQ-LOGIN-04 | "Password is required" | "Epic sadface: Password is required" | **Difference:** same prefix (see Q1) |
| REQ-LOGIN-05 | Message saying the credentials do not match | "Epic sadface: Username and password do not match any user in this service" | Matches the intent; exact wording not specified (see Q2) |
| REQ-LOGIN-06 | Message saying the account is locked | "Epic sadface: Sorry, this user has been locked out." and the user stays on the login page | Matches the intent; exact wording not specified (see Q3) |
| REQ-LOGIN-07 | The user can close the error | An "X" button (`data-test="error-button"`, `aria-label="Dismiss error"`) removes the message and the field icons | Matches. ~~**Observation:** the `input_error` class stays on the inputs after closing (see Q10)~~ **Corrected 2026-11-27 (stage 3):** `input_error` is a base class present from page load. The real error marker is the `error` class (red border `rgb(226, 35, 26)`), and it is removed on close. The border returns to `rgb(237, 237, 237)`. No defect, and the PO confirmed this in the Q10 update. |
| REQ-LOGIN-08 | Password characters hidden | The password input is `type="password"` | Matches |

Other things I observed that the requirement does not cover (for information, not counted as defects):

- **Both fields empty:** only the username error is shown.
- **Case and whitespace:** usernames are case-sensitive and are not trimmed.
- **Enter key:** pressing Enter submits the form (the button is `type="submit"` inside a `<form>`).
- **Values after a failed login:** both fields keep their values after a failed attempt.
- **Accepted usernames on the page:** they are listed together with the shared password "secret_sauce".
- **Direct URL access while logged out:** opening `/inventory.html` without a session redirects to the login page with "Epic sadface: You can only access '/inventory.html' when you are logged in."
- **Session cookie:** the session is a client-side cookie `session-username=<username>`. It is not HttpOnly and has no server token.
- **No visible labels:** the inputs have no `<label>` elements, only placeholder text and `aria-label`. The error container has `role="alert"`.
- **Missing autocomplete:** the inputs have no `autocomplete` attribute, and Chrome logs a DOM warning suggesting `current-password`.
- **Third-party console errors:** the browser console shows 401 errors from a third-party telemetry endpoint (backtrace.io). They do not affect login.

---

## 5. Risks

| # | Risk | Likelihood | Impact | Why it matters |
|---|------|-----------|--------|----------------|
| R1 | Valid users cannot log in (redirect fails, or credential checking breaks) | Medium | **Critical** | Login is the entry point to the whole app. If it breaks, no one can browse or order, and every downstream feature and test is blocked. |
| R2 | A locked-out user gets in, or wrong credentials are accepted | Low | **Critical** | Access-control failure; a security and compliance risk. |
| R3 | Error message text changes (copy edits, the "Epic sadface:" prefix) | High | Medium | Tests that check exact text will fail even though the behavior is correct. Tests need an agreed assertion strategy (exact vs contains). |
| R4 | Weak session handling: a client-side cookie with only the username can be forged to skip login | Medium | High | Anyone could log in as any user by setting a cookie. This is acceptable in a demo app but critical in production. |
| R5 | Password not masked, or exposed (wrong input type, kept in the field after an error, autocomplete) | Low | High | Privacy and shoulder-surfing exposure. |
| R6 | Error state not fully reset after closing the error (field styling stays) | Low (originally logged as observed; corrected in stage 3, it resets correctly) | Low | Cosmetic and UX confusion; could hide a real state bug. |
| R7 | Validation order or priority changes (both fields empty) | Medium | Low | Users may see a misleading error, and tests may break. |
| R8 | Accessibility gaps (no visible labels, only placeholders) | Medium | Medium | Screen-reader and WCAG problems could exclude users or create legal exposure. |

---

## 6. Test Scope

**In scope**
- Login page elements: the username field, password field and Login button (REQ-LOGIN-01).
- A successful login with `standard_user`, landing on the Products page (REQ-LOGIN-02).
- Validation of empty fields: empty username, empty password, both empty (REQ-LOGIN-03, -04).
- Invalid credentials: wrong password, wrong username, both wrong, wrong case, extra whitespace (REQ-LOGIN-05).
- The locked-out user (REQ-LOGIN-06).
- Closing the error message (REQ-LOGIN-07).
- Password masking (REQ-LOGIN-08).
- Submitting with the Enter key (ASSUMPTION: this counts as equivalent to clicking Login; see Q7).

**Out of scope** (not stated in the requirement; pending answers to the open questions)
- Logout, session persistence and timeout (Q14).
- Direct URL access while logged out (Q14). It was observed but is not a requirement.
- Behavior of the other demo users (`problem_user`, `performance_glitch_user`, `error_user`, `visual_user`) after login (Q13).
- Account lockout or rate limiting after repeated failures (Q11).
- Registration, password reset and "remember me". None of these exist in the app or the requirement.
- Performance and load testing of login.
- Cross-browser and mobile testing beyond the default Chromium run (Q16).
- Product browsing and ordering (separate features).

---

## 7. Test Types

| Test type | Applies? | Why / what it covers |
|-----------|----------|----------------------|
| **Functional (positive)** | Yes | Core happy path: a valid login lands on the Products page (REQ-LOGIN-02), and the page elements are present (REQ-LOGIN-01). |
| **Negative** | Yes | Most of the acceptance criteria are failure paths: empty fields, wrong credentials, the locked user (REQ-LOGIN-03 to -06). |
| **Boundary / edge cases** | Yes | Empty vs whitespace-only input, leading and trailing spaces, case changes, very long strings, special and Unicode characters. These probe where the validation is applied (REQ-LOGIN-03 to -05). |
| **UI** | Yes | The error banner appears with the correct text, the close button removes it (REQ-LOGIN-07), and the password is masked (REQ-LOGIN-08). |
| **Security** | Yes, light | Password masking (REQ-LOGIN-08), the locked user is refused (REQ-LOGIN-06), basic injection strings (`' OR '1'='1`, `<script>`) are rejected cleanly. A deeper session or cookie review is an observation only (R4) and needs a Product Owner decision. |
| **Accessibility** | Yes, basic | The error uses `role="alert"`, the close button has an `aria-label`, and the inputs have accessible names. Keyboard-only login (Tab and Enter) is possible. A full WCAG audit is out of scope unless requested (Q16). |
| **Usability** | Yes, light | Submitting with Enter, whether field values are kept after an error, and the clarity of error messages. |
| **Regression (automated)** | Yes | Login is a prerequisite for every other feature, so its happy path and key negative tests belong in the smoke and regression suite. |
| **Performance / load** | No | Not stated in the requirement. |
| **Compatibility** | No (for now) | Chromium only, pending Q16. |

---

## 8. Test Data Needed

**Users (from the requirement)**

| Purpose | Username | Password |
|---------|----------|----------|
| Valid user | `standard_user` | `secret_sauce` |
| Locked user | `locked_out_user` | `secret_sauce` |

**Negative and edge-case inputs**

| Case | Username | Password | Expected (observed) |
|------|----------|----------|---------------------|
| Both empty | *(empty)* | *(empty)* | "Username is required" |
| Empty username | *(empty)* | `secret_sauce` | "Username is required" |
| Empty password | `standard_user` | *(empty)* | "Password is required" |
| Wrong password | `standard_user` | `wrong_pass` | "do not match" message |
| Unknown username | `unknown_user` | `secret_sauce` | "do not match" message |
| Both wrong | `unknown_user` | `wrong_pass` | "do not match" message |
| Username in wrong case | `Standard_User` | `secret_sauce` | "do not match" message |
| Password in wrong case | `standard_user` | `SECRET_SAUCE` | "do not match" message (ASSUMPTION: not yet observed) |
| Leading or trailing spaces | `" standard_user "` | `secret_sauce` | "do not match" message |
| Whitespace-only username | `"   "` | `secret_sauce` | Unknown: not observed (see Q6) |
| Locked user with wrong password | `locked_out_user` | `wrong_pass` | Unknown: which error wins? (ASSUMPTION: "do not match") |
| SQL injection string | `' OR '1'='1` | `' OR '1'='1` | "do not match" message, no crash |
| Script injection string | `<script>alert(1)</script>` | `x` | "do not match" message, no script runs |
| Long string | 256+ characters | 256+ characters | "do not match" message, layout intact (see Q15) |
| Unicode | `用户` / `ñandú` | `secret_sauce` | "do not match" message |

**Configuration** (`src/test/resources/config.properties`)
- `base.url=https://www.saucedemo.com`
- Valid and locked usernames, and the password. No hardcoded credentials in tests.

**Locators confirmed on the live page** (for the `/automate` stage)
- `[data-test="username"]`, `[data-test="password"]`, `[data-test="login-button"]`
- `[data-test="error"]` (the error text, `role="alert"`), `[data-test="error-button"]` (close)
- `[data-test="title"]` on the Products page (text "Products")
