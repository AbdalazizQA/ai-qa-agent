# Requirements Traceability Matrix: Login

- **Sources:** `requirements/login.md`, `outputs/login/analysis.md`, `outputs/login/po-answers.md`
- **Test cases:** `outputs/login/test-cases.csv`
- **Date:** 2026-11-27

## Matrix

| Req ID | Requirement | Priority | Test Case IDs | # TCs | Coverage |
|--------|-------------|----------|---------------|-------|----------|
| REQ-LOGIN-01 | The login page shows the username field, password field and Login button | High | TC_LOGIN_001, TC_LOGIN_002, TC_LOGIN_003 | 3 | Covered |
| REQ-LOGIN-02 | A valid user is redirected to the Products page | High | TC_LOGIN_004, TC_LOGIN_005 | 2 | Covered |
| REQ-LOGIN-03 | Empty username shows "Username is required" (PO Q1, Q4) | Medium | TC_LOGIN_006, TC_LOGIN_007 | 2 | Covered |
| REQ-LOGIN-04 | Empty password shows "Password is required" (PO Q1) | Medium | TC_LOGIN_008 | 1 | Covered |
| REQ-LOGIN-05 | Wrong username or password shows the "do not match" message (PO Q2, Q5, Q6) | High | TC_LOGIN_009, TC_LOGIN_010, TC_LOGIN_011, TC_LOGIN_012, TC_LOGIN_013, TC_LOGIN_022, TC_LOGIN_023 | 7 | Covered |
| REQ-LOGIN-06 | A locked-out user cannot log in and sees the locked-out message (PO Q3) | High | TC_LOGIN_014, TC_LOGIN_015 | 2 | Covered |
| REQ-LOGIN-07 | The user can close the error message, which resets the whole error state (PO Q10) | Low | TC_LOGIN_016, TC_LOGIN_017, TC_LOGIN_018 | 3 | Covered |
| REQ-LOGIN-08 | The password field hides the typed characters | High | TC_LOGIN_019 | 1 | Covered |
| REQ-LOGIN-09 | Pressing Enter is a valid way to log in (**new**, added from PO Q7) | High | TC_LOGIN_020, TC_LOGIN_021 | 2 | Covered |

**Coverage gaps:** None. Every requirement has at least one test case.

## Notes on changes from the analysis

- **REQ-LOGIN-09** is new. The analysis listed Enter-key submission as an ASSUMPTION. PO Q7 brought it into scope, so it now has its own ID.
- **REQ-LOGIN-07** was widened by PO Q10. Closing the error must also clear the red field borders (TC_LOGIN_018). *Correction (stage 3):* the analysis wrongly reported that the borders stay after closing. Re-inspection showed the `error` class and the red border ARE removed. The PO confirmed in the Q10 update that there is no defect.
- **Out of scope** (PO Q11 to Q16): lockout and rate limiting, credentials shown on the page, the other demo users, session and logout handling, field length limits, accessibility and cross-browser testing. No test cases were written for these.

## Blocked (no test case until the PO answers)

| Item | Question | Why it is blocked |
|------|----------|-------------------|
| B1 | **Q9:** When only one field is wrong, should both fields be highlighted in red? | Q9 is unanswered, so the expected highlighting cannot be defined. |
| B2 | Is the password case-sensitive (for example, is `SECRET_SAUCE` rejected)? | PO Q5 covers usernames only. |
| B3 | Is a username made only of spaces (`"   "`) treated as empty ("Username is required") or as a wrong username ("do not match")? | Q6 covers spaces around a real username, not a username that is only spaces. |
| B4 | For `locked_out_user` with a wrong password, which message wins: "locked out" or "do not match"? | Not stated, and it was raised as an ASSUMPTION in the analysis. |
