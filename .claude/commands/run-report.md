---
description: Stage 4 - Run the tests and write the Test Summary Report
argument-hint: <feature-name>
---

You are a QA lead. Run the tests for the feature "$ARGUMENTS" and write the final Test Summary Report.

Inputs:
- `outputs/$ARGUMENTS/analysis.md`, `test-cases.csv`, `rtm.md`, and `po-answers.md` if it exists.

Steps:
1. Run `mvn clean test`, then `mvn allure:report`. Do not change any test or application code in this stage.
   If tests fail, report them exactly as they are.
2. Read the results from `target/surefire-reports` and `target/allure-results`.
3. Write `outputs/$ARGUMENTS/test-summary-report.md` with these sections:
    - **Overview**: feature, application, date, environment (OS, browser, Java and Playwright versions), and who/what executed the run.
    - **Scope**: what was tested and what was out of scope (from the analysis and PO answers).
    - **Execution Results**: a table with totals: planned, automated, manual-only, executed, passed, failed, skipped, pass rate, and duration.
    - **Results by Requirement**: one row per REQ ID with its test cases and status, based on the RTM.
    - **Defects**: every failed test with test case ID, severity, steps, expected vs actual, and evidence (screenshot path).
      If there are none, say so clearly.
    - **Corrections During Testing**: any expected result or analysis finding that was corrected during the cycle,
      what was wrong, how it was verified, and its impact.
    - **Observations (not defects)**: findings outside the requirement's scope worth raising (security, accessibility, usability).
    - **Open Items**: blocked or unanswered questions and manual-only test cases not executed.
    - **Risks and Recommendation**: remaining risks and a clear Go / No-Go recommendation with the reason.
    - **How to Reproduce**: the commands to run the tests and open the Allure report.
4. Finish by printing: pass rate, number of defects, the Go / No-Go recommendation, and the path to the Allure report.