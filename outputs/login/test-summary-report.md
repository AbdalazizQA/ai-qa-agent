# Test Summary Report: Login

## 1. Overview

| Item | Value |
|------|-------|
| Feature | Login |
| Application | Swag Labs (https://www.saucedemo.com) |
| Requirement | `requirements/login.md` |
| Report date | 2026-11-27 |
| OS | Windows 11 Pro (10.0.26300) |
| Browser | Chromium (headless), the build bundled with Playwright 1.64.0 |
| Java | Oracle JDK 26.0.2.1 runtime, compiled with `--release 21` |
| Tooling | Maven 3.9.16, Playwright for Java 1.64.0, TestNG 7.11.0, Allure TestNG 2.29.1 (report 2.30.0) |
| Executed by | The AI QA Agent (Claude Code), automated run with `mvn clean test` |
| Sources | `analysis.md`, `po-answers.md`, `test-cases.csv`, `rtm.md` |

## 2. Scope

**In scope:** REQ-LOGIN-01 to REQ-LOGIN-09.
- Login page elements.
- Successful login with `standard_user`.
- Empty-field validation.
- Invalid credentials, including case-sensitivity (PO Q5) and spaces that are not trimmed (PO Q6).
- The locked-out user.
- Closing the error, including resetting the field styling (PO Q10).
- Password masking.
- Login with the Enter key and with the keyboard only (PO Q7, which added REQ-LOGIN-09).
- Light security checks: SQL injection and script injection strings.

**Out of scope** (PO Q11 to Q16):
- Lockout and rate limiting.
- Credentials shown on the login page.
- The other demo users (`problem_user`, `performance_glitch_user`, `error_user`, `visual_user`).
- Session, logout and direct URL access.
- Field length limits.
- A full accessibility audit.
- Cross-browser and mobile testing.
- Performance testing.

## 3. Execution Results

| Metric | Value |
|--------|-------|
| Test cases planned | 23 |
| Automated | 23 |
| Manual-only | 0 |
| Executed | 23 login test cases (+1 environment smoke test, `SetupSmokeTest`) = 24 TestNG results |
| Passed | 24 (23 login + 1 smoke) |
| Failed | 0 |
| Skipped | 0 |
| Broken (Allure) | 0 |
| Pass rate | **100%** (23/23 login, 24/24 overall) |
| Duration | 27.8 s for the test run (Allure); 30.8 s Maven total for `mvn clean test` |

How the 23 test cases map to test methods:
- **21 TestNG methods** in `LoginTests`.
- **Two data-driven methods** with three rows each: TC_LOGIN_009–011 and TC_LOGIN_012, 013, 022.
- **Separate Allure entries:** each data row appears under its own test case ID.

Sources: `target/surefire-reports/TEST-TestSuite.xml` and `target/allure-results` (24 `*-result.json` files, 0 failure attachments).

## 4. Results by Requirement

| Req ID | Requirement | Priority | Test Cases | Executed | Passed | Status |
|--------|-------------|----------|------------|----------|--------|--------|
| REQ-LOGIN-01 | Username field, password field and Login button shown | High | TC_LOGIN_001, 002, 003 | 3 | 3 | ✅ Pass |
| REQ-LOGIN-02 | Valid user redirected to the Products page | High | TC_LOGIN_004, 005 | 2 | 2 | ✅ Pass |
| REQ-LOGIN-03 | Empty username shows "Username is required" | Medium | TC_LOGIN_006, 007 | 2 | 2 | ✅ Pass |
| REQ-LOGIN-04 | Empty password shows "Password is required" | Medium | TC_LOGIN_008 | 1 | 1 | ✅ Pass |
| REQ-LOGIN-05 | Wrong credentials show the "do not match" message | High | TC_LOGIN_009, 010, 011, 012, 013, 022, 023 | 7 | 7 | ✅ Pass |
| REQ-LOGIN-06 | Locked-out user is refused and sees the locked message | High | TC_LOGIN_014, 015 | 2 | 2 | ✅ Pass |
| REQ-LOGIN-07 | The error can be closed and the error state resets | Low | TC_LOGIN_016, 017, 018 | 3 | 3 | ✅ Pass |
| REQ-LOGIN-08 | Password characters are hidden | High | TC_LOGIN_019 | 1 | 1 | ✅ Pass |
| REQ-LOGIN-09 | Login with the Enter key (PO Q7) | High | TC_LOGIN_020, 021 | 2 | 2 | ✅ Pass |

Coverage: 9 of 9 requirements are covered and passing, with no coverage gaps.

## 5. Defects

**No defects were found in this cycle.** All 23 login test cases passed, and no application behaviour differed from an expected result. The defect predicted in stage 2 for TC_LOGIN_018 turned out to come from a wrong analysis finding (see section 6).

## 6. Corrections During Testing

### C1: The "error state not reset" finding was wrong (TC_LOGIN_018, REQ-LOGIN-07, PO Q10)

**What was wrong**
- The analysis (stage 1) said that after the error is closed, the `input_error` class stays on the fields, so the red borders are not reset.
- The PO answered Q10 based on that finding and called it a defect.
- In stage 2, TC_LOGIN_018 was written to expect a failure.

**How it was verified (stage 3, live page, Playwright MCP)**

| Field state | CSS classes | Bottom border colour |
|-------------|-------------|----------------------|
| Page just loaded | `input_error form_input` | `rgb(237, 237, 237)` (grey) |
| Error shown | `input_error form_input error` | `rgb(226, 35, 26)` (red) |
| Error closed | `input_error form_input` | `rgb(237, 237, 237)` (grey) |

- `input_error` is a base class that is on the fields from page load. It does not mean the field is in error.
- The class that actually marks the error is `error`, and closing the error removes it.

**Fix**
- TC_LOGIN_018 now checks the class that really marks the error.
- It first confirms that the `error` class and the red border are there, and then that both are gone after closing. This makes the check stricter, not weaker.
- `analysis.md` (section 4 and risk R6), `test-cases.csv` and `rtm.md` were corrected.
- The PO has since confirmed: "Q10: Update: Re-verified during automation. No defect."

**Impact**
- The defect count went from 1 expected to 0.
- REQ-LOGIN-07 is fully met.
- No change to scope.

### C2: Project files reverted between stages (process issue)

**What happened**
- Before this run, the whole `src/test` tree had been removed: test code, page objects, `BaseTest`, `ConfigReader`, `config.properties` and `SetupSmokeTest`.
- The C1 corrections in `analysis.md`, `test-cases.csv`, `rtm.md` and the `pom.xml` Allure results directory were also reverted.

**How it was handled**
- With the user's approval, the stage 3 files were restored exactly as written in stage 3.
- Then this run was executed.
- This goes against the "do not change code in this stage" rule. It was done on purpose and with approval.

**Impact**
- The results above come from the restored stage 3 suite.
- The test team should put the project under version control (`git init`) so that work cannot be lost silently.

## 7. Observations (not defects)

These came up during the analysis. They are outside the agreed scope, and the PO has marked them out of scope for this release.

| # | Area | Observation | Suggestion |
|---|------|-------------|------------|
| O1 | Security | Every accepted username and the shared password are printed on the login page. | Acceptable only in a demo environment; must not reach production (PO Q12). |
| O2 | Security | The session is a client-side cookie `session-username=<username>`. It is not HttpOnly and has no server token, so it could be forged to skip login. | Use a server-issued, HttpOnly, Secure session token. |
| O3 | Security | There is no lockout or rate limit after repeated failed logins. | Consider a lockout or throttling policy (PO Q11). |
| O4 | Accessibility | The inputs have no visible `<label>`, only placeholder text and `aria-label`. | Add visible labels (WCAG 3.3.2) (PO Q16). |
| O5 | Accessibility / usability | The inputs have no `autocomplete` attribute. Chrome logs a DOM warning suggesting `current-password`. | Add `autocomplete="username"` and `"current-password"`. |
| O6 | Usability | After a failed login the typed password stays in the field. | Accepted by the PO (Q8). Listed for information only. |
| O7 | Monitoring | The browser console shows 401 errors from the third-party telemetry endpoint `events.backtrace.io`. | Fix or remove the telemetry configuration. It does not affect login. |

## 8. Open Items

| # | Item | Status |
|---|------|--------|
| B1 | **Q9:** when only one field is wrong, should both fields be highlighted in red? | Unanswered. No test case. |
| B2 | Is the password case-sensitive (for example, `SECRET_SAUCE`)? | Unanswered. No test case. |
| B3 | Is a username of only spaces treated as empty or as a wrong username? | Unanswered. No test case. |
| B4 | Which message wins for `locked_out_user` with a wrong password? | Unanswered. No test case. |
| M1 | Manual-only test cases not executed | None. All 23 cases are automated and were executed. |
| P1 | Project is not under version control (see C2) | Open. Recommend `git init` and committing the suite. |

## 9. Risks and Recommendation

**Remaining risks**
- **Blocked items B1 to B4** are untested edge cases. Their impact is low to medium: they involve validation messages, not access control.
- **Single browser:** the only browser tested was Chromium.
- **Production security:** the security observations O1 to O3 would be serious in production, but the PO has put them out of scope for this release.
- **File loss:** test files were lost between stages (C2) and had to be restored. Until the suite is under version control, the delivered work can be lost again.

**Recommendation: ✅ GO** for the Login feature, in scope as defined above.

Reason:
- All 9 requirements are covered.
- All 23 test cases pass, including every High-priority case on access control.
- There are no open defects.
- The only finding that predicted a defect was investigated and shown to be a false positive; the PO confirmed it.
- The open items are low-impact edge cases waiting for PO answers, not failures.

Conditions:
- Get answers to B1 to B4 and add test cases for them in the next cycle.
- Put the project under version control.

## 10. How to Reproduce

Run from the project root (`D:\AI Agent Project`):

```bash
# Run all tests (headless)
mvn clean test

# Watch the browser while the tests run
mvn clean test -Dheadless=false

# Run only the login tests
mvn clean test -Dtest=LoginTests

# Generate the Allure report (reads target/allure-results)
mvn allure:report
#   then open: target/site/allure-maven-plugin/index.html
#   (serve it over HTTP, e.g. `mvn allure:serve`, because browsers block the
#    report's data files when opened via file://)
```

Configuration (base URL and test accounts) is in `src/test/resources/config.properties`. Any value can be overridden with `-D<key>=<value>`, for example `-Dbase.url=...`.
