---
description: Stage 3 - Automate the test cases with Playwright Java + TestNG
argument-hint: <feature-name>
---

You are a senior QA automation engineer. Automate the test cases for the feature "$ARGUMENTS".

Inputs:
- `outputs/$ARGUMENTS/test-cases.csv` (stage 2 output). If it does not exist, stop and tell me to run /testcases first.
- `outputs/$ARGUMENTS/analysis.md` (confirmed locators are listed at the end).
- Follow every rule in CLAUDE.md.

Steps:
1. Automate only the cases marked Automate = Yes.
2. Before writing locators, open the pages with the Playwright MCP tools and confirm every locator on the live page.
3. Create or reuse (do not duplicate if they already exist):
    - `src/test/resources/config.properties`: base.url, usernames, password.
    - `base/ConfigReader.java`: reads config.properties.
    - `base/BaseTest.java`: one Playwright and Browser per class, a new BrowserContext and Page per test method.
      Headless by default; allow `-Dheadless=false` to watch the run.
      On failure, attach a full-page screenshot to Allure with Allure.addAttachment.
    - Page Objects in `pages/` (e.g. LoginPage, ProductsPage): locators and actions only, no assertions.
    - Test class `tests/<Feature>Tests.java`.
4. Test design:
    - One test method per test case. Method name and TestNG description start with the test case ID,
      e.g. `@Test(description = "TC_LOGIN_001: ...")`.
    - Use a TestNG @DataProvider when several test cases differ only in their data (e.g. invalid credentials).
      The data rows must keep their test case IDs so each row is traceable.
    - Add Allure @Epic("Authentication"), @Feature("<Feature>"), @Severity mapped from priority
      (High = CRITICAL, Medium = NORMAL, Low = MINOR).
    - Use Allure.step("...", () -> {...}) for readable steps. Do not use the @Step annotation.
    - Use Playwright's assertThat(...) for page and element checks; error texts are checked with CONTAINS.
5. Run `mvn test`.
    - If a test fails because of TEST CODE (bad locator, timing, compile error), fix the test code and re-run.
    - If a test fails because the APPLICATION behaves differently from the expected result, DO NOT change the
      assertion, do not skip or disable the test. Leave it failing and record it as a defect.
    - Never weaken an assertion just to make a test pass.
6. Finish by printing: files created, number of automated tests, pass/fail counts, and for each failing test
   whether it is a test-code issue (should be zero by now) or an application defect, with the evidence.
   Do not write the final report yet.