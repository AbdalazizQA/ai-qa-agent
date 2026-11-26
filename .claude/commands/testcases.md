---
description: Stage 2 - Write manual test cases and an RTM from the analysis
argument-hint: <feature-name>
---

You are a senior QA engineer. Write manual test cases for the feature "$ARGUMENTS".

Inputs:
- `requirements/$ARGUMENTS.md` (the requirement)
- `outputs/$ARGUMENTS/analysis.md` (stage 1 output). If it does not exist, stop and tell me to run /analyze first.
- `outputs/$ARGUMENTS/po-answers.md` (Product Owner answers), if it exists.

Rules:
- Where the PO answered a question, follow the answer. It overrides the original requirement text.
- Where a question is still unanswered, do not write a test case for it. List it under "Blocked" instead.
- Cover positive, negative, boundary, UI, light security and keyboard cases from the analysis's Test Types section.
- Each test case checks one thing. Steps must be clear enough for someone who has never seen the app.
- Expected results must be specific and observable (exact URL, exact element, text the error CONTAINS).
- Mark each case Automate = Yes or No. Use No only when automation adds little value, and give the reason.

Outputs:
1. `outputs/$ARGUMENTS/test-cases.csv` with columns:
   ID, Title, Requirement ID, Priority, Type, Preconditions, Steps, Test Data, Expected Result, Automate, Notes
    - IDs: TC_<FEATURE>_001, TC_<FEATURE>_002, ...
    - Put numbered steps in one cell separated by " | ".
    - Quote every field so commas inside text do not break the CSV.
2. `outputs/$ARGUMENTS/rtm.md`: a Requirements Traceability Matrix mapping every REQ ID to its test case IDs.
   Flag any requirement with no test case as a coverage gap.
3. Finish by printing: total test cases, count by type and by priority, how many will be automated,
   any coverage gaps, and any blocked items. Do not start automation.