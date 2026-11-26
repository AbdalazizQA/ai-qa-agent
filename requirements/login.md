# Feature: Login

## User Story
As a registered customer, I want to log in with my username and password
so that I can browse products and place orders.

## Acceptance Criteria
1. The login page shows a username field, a password field, and a Login button.
2. A user with valid credentials is redirected to the Products page after clicking Login.
3. If the username is empty, an error message is shown: "Username is required".
4. If the password is empty, an error message is shown: "Password is required".
5. If the username or password is wrong, an error message is shown telling the user
   that the credentials do not match.
6. A locked-out user cannot log in and sees a message saying the account is locked.
7. The error message can be closed by the user.
8. The password field must hide the characters typed.

## Test Accounts
- Valid user: standard_user / secret_sauce
- Locked user: locked_out_user / secret_sauce