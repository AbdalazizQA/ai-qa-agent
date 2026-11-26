# Product Owner Answers — Login

- Q1: The "Epic sadface: " prefix is accepted. Tests should check that the error CONTAINS the stated text.
- Q2: Yes, "Username and password do not match any user in this service" is the agreed wording.
- Q3: Yes, "Sorry, this user has been locked out." is accepted.
- Q4: Yes, when both fields are empty, showing only "Username is required" is intended.
- Q5: Usernames are case-sensitive.
- Q6: Spaces are not trimmed; " standard_user " must be rejected.
- Q7: Pressing Enter is a valid way to log in. Add it to scope.
- Q8: Keeping the field values after a failed login is acceptable.
- Q10: Update: Re-verified during automation. No defect — closing the error resets the state correctly.
- Q11, Q12, Q14, Q15, Q16: Out of scope for this release.
- Q13: Out of scope; only standard_user and locked_out_user are in scope.