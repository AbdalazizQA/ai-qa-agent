# AI QA Agent

An AI-driven QA workflow that takes a written requirement and carries it through
the full testing cycle: analysis → manual test cases → automation → execution → report.
Application under test: Swag Labs (https://www.saucedemo.com).

## Workflow (one command per stage)
1. /analyze <feature>    → outputs/<feature>/analysis.md
2. /testcases <feature>  → outputs/<feature>/test-cases.csv
3. /automate <feature>   → Playwright Java tests under src/test/java
4. /run-report <feature> → runs tests, writes outputs/<feature>/test-summary-report.md

Each stage reads the output of the previous stage. Never skip a stage.

## Tech stack
- Java 21, Maven, Playwright for Java, TestNG, Allure Report
- Run tests: `mvn test`  |  Allure report: `mvn allure:report`

## Folder conventions
- requirements/<feature>.md          → input requirements (never modify these)
- outputs/<feature>/                 → all generated QA documents
- src/test/java/com/qa/agent/pages/  → Page Objects
- src/test/java/com/qa/agent/tests/  → Test classes
- src/test/java/com/qa/agent/base/   → BaseTest (browser setup/teardown)
- src/test/resources/config.properties → base URL and test credentials

## Automation rules
- Page Object Model: locators and actions live in page classes, assertions live in tests.
- Before writing any locator, open the real page with the Playwright MCP tools and inspect it.
  Never guess locators.
- Locator priority: data-test attribute → getByRole → getByText → CSS. No XPath unless unavoidable.
- No Thread.sleep. Rely on Playwright auto-waiting and assertThat(...).
- No hardcoded URLs or credentials in tests; read them from config.properties.
- Every test method has a TestNG description and Allure @Epic/@Feature/@Severity annotations,
  and its name maps to a test case ID (e.g. TC_LOGIN_001).
- One browser context per test so tests are independent.

## Analysis rules
- Only test what the requirement states. Anything not stated is an assumption: label it
  "ASSUMPTION" or raise it as an open question. Never invent behavior.
- Give every testable requirement an ID (REQ-<FEATURE>-01, ...) so test cases can trace back to it.