# Login Module — Test Cases

**Module:** Authentication & Authorization  
**Total Test Cases:** 22  
**Tester:** Deep Ghevariya  
**Priority Distribution:** Critical: 5 | High: 8 | Medium: 6 | Low: 3  

---

## Test Cases

| TC ID | Test Case Title | Pre-Conditions | Test Steps | Expected Result | Priority | Type | Status |
|-------|----------------|----------------|-----------|----------------|----------|------|--------|
| TC_LGN_001 | Valid Admin Login | App is accessible; valid credentials exist | 1. Navigate to login URL<br>2. Enter username: Admin<br>3. Enter password: admin123<br>4. Click Login | Dashboard loads; URL contains /dashboard; Username visible | Critical | Smoke | — |
| TC_LGN_002 | Valid HR Manager Login | HR account exists | 1. Enter HR username<br>2. Enter HR password<br>3. Click Login | Dashboard loads with HR-specific menu options | High | Functional | — |
| TC_LGN_003 | Valid Employee Login | Employee account exists | 1. Enter employee creds<br>2. Click Login | Dashboard with limited menu (no Admin) | High | Functional | — |
| TC_LGN_004 | Invalid Username | — | 1. Enter wrong username<br>2. Enter any password<br>3. Click Login | Error: "Invalid credentials" message shown | Critical | Negative | — |
| TC_LGN_005 | Invalid Password | — | 1. Enter valid username<br>2. Enter wrong password<br>3. Click Login | Error: "Invalid credentials" message shown | Critical | Negative | — |
| TC_LGN_006 | Empty Username | — | 1. Leave username blank<br>2. Enter valid password<br>3. Click Login | Validation: "Required" error on username field | High | Negative | — |
| TC_LGN_007 | Empty Password | — | 1. Enter valid username<br>2. Leave password blank<br>3. Click Login | Validation: "Required" error on password field | High | Negative | — |
| TC_LGN_008 | Both Fields Empty | — | 1. Leave both fields blank<br>2. Click Login | Both fields show "Required" validation errors | Medium | Negative | — |
| TC_LGN_009 | Login Page UI Elements | — | 1. Navigate to login page<br>2. Observe all elements | Username field, Password field, Login button, Logo, Forgot Password link all visible | Medium | UI | — |
| TC_LGN_010 | Logout Functionality | User is logged in | 1. Click user dropdown<br>2. Click Logout | Redirected to login page; session cleared | Critical | Smoke | — |
| TC_LGN_011 | Back Button After Logout | User has logged out | 1. Logout<br>2. Press browser Back button | Should NOT show protected page; stays on login | High | Security | — |
| TC_LGN_012 | Session Timeout | User logged in | 1. Login<br>2. Idle for session timeout period<br>3. Perform action | Redirected to login with timeout message | High | Security | — |
| TC_LGN_013 | Remember Me | — | 1. Login with Remember Me checked<br>2. Close browser<br>3. Reopen and navigate | Session persisted or username pre-filled | Medium | Functional | — |
| TC_LGN_014 | Forgot Password Link | — | 1. Click "Forgot Password"<br>2. Observe navigation | Navigated to password reset page | Medium | Functional | — |
| TC_LGN_015 | Forgot Password — Valid Email | — | 1. Click Forgot Password<br>2. Enter registered email<br>3. Submit | Success message: "Reset link sent" | High | Functional | — |
| TC_LGN_016 | Forgot Password — Invalid Email | — | 1. Click Forgot Password<br>2. Enter unregistered email<br>3. Submit | Error: Email not found | Medium | Negative | — |
| TC_LGN_017 | Username Case Sensitivity | — | 1. Enter "ADMIN" (uppercase)<br>2. Enter valid password<br>3. Click Login | Login should FAIL (case-sensitive) | High | Functional | — |
| TC_LGN_018 | Password Masking | — | 1. Navigate to login<br>2. Type in password field<br>3. Observe characters | Characters are masked (•••) by default | Medium | Security | — |
| TC_LGN_019 | Show/Hide Password Toggle | — | 1. Enter password<br>2. Click eye icon | Password toggles visible/hidden | Low | UI | — |
| TC_LGN_020 | SQL Injection in Username | — | 1. Enter: ' OR '1'='1<br>2. Enter any password<br>3. Click Login | Login fails; no SQL error; secure response | Critical | Security | — |
| TC_LGN_021 | XSS in Username Field | — | 1. Enter: <script>alert(1)</script><br>2. Click Login | Script is not executed; sanitized response | High | Security | — |
| TC_LGN_022 | Multiple Failed Login Attempts | — | 1. Enter wrong creds 5 times<br>2. Try again | Account lock or CAPTCHA shown after X attempts | High | Security | — |

---

## Test Data

| Data Type | Values Used |
|-----------|------------|
| Valid Admin | Admin / admin123 |
| Invalid User | wronguser / wrongpass |
| Empty fields | empty string / empty string |
| SQL Injection | `' OR '1'='1` |
| XSS | `<script>alert('XSS')</script>` |

---

## Coverage Summary

| Area | Covered | Test IDs |
|------|---------|---------|
| Positive Login | ✅ | 001, 002, 003 |
| Negative Login | ✅ | 004, 005, 006, 007, 008 |
| Session Management | ✅ | 010, 011, 012, 013 |
| Password Reset | ✅ | 014, 015, 016 |
| Security | ✅ | 017, 020, 021, 022 |
| UI/UX | ✅ | 009, 018, 019 |
