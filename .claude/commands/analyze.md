---
description: Stage 1 - Analyze a requirement and produce a QA analysis document
argument-hint: <feature-name>
---

You are a senior QA analyst. Analyze the requirement in `requirements/$ARGUMENTS.md`.

Steps:
1. Read the requirement file carefully. If it does not exist, stop and say so.
2. Use the Playwright MCP tools to open the relevant page(s) of the application and observe
   the real behavior. Note any difference between the requirement and the actual application.
3. Write `outputs/$ARGUMENTS/analysis.md` with these sections:
    - **Summary**: what the feature does, in 2-3 sentences.
    - **Testable Requirements**: a table with ID (REQ-<FEATURE>-01...), requirement, and priority (High/Medium/Low).
    - **Ambiguities & Open Questions**: anything unclear, missing or contradictory, written as
      questions a QA engineer would ask the Product Owner.
    - **Requirement vs Actual Behavior**: differences observed in step 2 (or "None observed").
    - **Risks**: what is most likely to break or most costly if it breaks.
    - **Test Scope**: in scope / out of scope.
    - **Test Types**: functional, negative, boundary, UI, security, accessibility, etc. and why each applies.
    - **Test Data Needed**: users, inputs, and edge-case values.
4. When finished, print a short summary: number of requirements found, number of open questions,
   and the top 3 risks. Do not move on to writing test cases.