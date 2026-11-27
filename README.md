# AI QA Agent — From Requirement to Test Report

[![Login Tests](https://github.com/AbdalazizQA/ai-qa-agent/actions/workflows/tests.yml/badge.svg)](https://github.com/AbdalazizQA/ai-qa-agent/actions/workflows/tests.yml)

An AI-driven QA workflow built on **Claude Code** and **Playwright MCP**. It takes a written requirement and carries it through the full testing cycle (analysis, manual test cases, automation, execution, and a test summary report), with a human QA engineer reviewing every stage.

**Application under test:** [Swag Labs](https://www.saucedemo.com) — Login feature

---

## How it works

```
requirements/login.md
        │
        ▼  /analyze login      → outputs/login/analysis.md
        │                        testable requirements, open questions, risks,
        │                        requirement vs. actual behavior (checked live with Playwright MCP)
        ▼  (PO answers)        → outputs/login/po-answers.md
        │
        ▼  /testcases login    → outputs/login/test-cases.csv + rtm.md
        │                        23 test cases with full requirement traceability
        ▼  /automate login     → src/test/java/...
        │                        Playwright Java + TestNG + Page Object Model, locators verified on the live page
        ▼  /run-report login   → outputs/login/test-summary-report.md + Allure report
                                 results, defects, observations, Go / No-Go recommendation
```

Each stage is a Claude Code custom command in [`.claude/commands/`](.claude/commands). The project rules the agent follows (POM, locator priority, no hardcoded data, "never weaken an assertion to make a test pass") live in [`CLAUDE.md`](CLAUDE.md).

---

## Highlights

- **Caught a requirement mismatch before any test was written.** The requirement said `"Username is required"`, but the app shows `"Epic sadface: Username is required"`. The analysis raised it as an open question, and the PO decided that tests should check the text with *contains*.
- **Self-correcting workflow.** The analysis first reported a possible defect: the error styling seemed to stay after closing the error. During automation the agent found that the `input_error` class is present from page load and that the real error marker is the `error` class. The test was made *stricter*, the documents were corrected, and the finding was verified manually in browser DevTools.
- **Human in the loop.** The QA engineer acts as Product Owner, answers the open questions, and reviews every stage's output before the next one runs.
- **Traceability.** Every test maps to a requirement ID through the RTM, and every automated test is named after its test case ID.

---

## Tech stack

| Area | Tools |
|------|-------|
| AI agent | Claude Code (custom commands), Playwright MCP |
| Automation | Java 21, Playwright for Java, TestNG, Page Object Model, Data-Driven Testing (@DataProvider) |
| Reporting | Allure Report (Epic / Feature / Severity, screenshots on failure) |
| Build & CI | Maven, Git, GitHub Actions |

---

## Project structure

```
.claude/commands/          AI agent stages: analyze, testcases, automate, run-report
CLAUDE.md                  Rules the agent follows
requirements/              Input requirements
outputs/login/             Generated QA documents (analysis, PO answers, test cases, RTM, summary report)
src/test/java/com/qa/agent/
  ├── base/                BaseTest (browser lifecycle, screenshots), ConfigReader
  ├── pages/               Page Objects (LoginPage, ProductsPage)
  └── tests/               LoginTests
src/test/resources/        config.properties
.github/workflows/         CI pipeline
```

---

## Run it

**Requirements:** Java 21+, Maven

```bash
mvn clean test                    # run all tests (headless)
mvn clean test -Dheadless=false   # watch the browser
mvn allure:serve                  # open the Allure report
```

**Run the AI workflow** (requires [Claude Code](https://claude.com/claude-code) and Playwright MCP):

```bash
claude mcp add playwright -- npx @playwright/mcp@latest
claude
/analyze login
/testcases login
/automate login
/run-report login
```

CI runs the full suite on every push and uploads the Allure report as a build artifact.

---

## Results (Login)

| Metric | Value |
|--------|-------|
| Test cases | 23 |
| Automated | 23 |
| Pass rate | 100% |
| Defects | 0 |
| Recommendation | Go |

See the full [Test Summary Report](outputs/login/test-summary-report.md).

---

**Author:** Abdalaziz Zinati, QA Automation Engineer · [LinkedIn](https://linkedin.com/in/abdalaziz-zinati-b737046a)
