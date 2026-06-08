# HRM System — Test Plan v1.0

**Document Control**

| Field | Details |
|-------|---------|
| Document Title | HRM System Test Plan |
| Version | 1.0 |
| Author | Deep Ghevariya |
| Organization | Seravion Technologies |
| Email | deepghevariya8890@gmail.com |
| Date | April 2025 |
| Status | Active |
| Review Date | June 2025 |

---

## 1. Introduction

### 1.1 Purpose
This Test Plan outlines the strategy, scope, approach, and resources required to test the **Human Resource Management (HRM) System** based on OrangeHRM Open Source.

### 1.2 Objectives
- Verify all functional requirements of the HRM system
- Ensure system quality through planned testing cycles
- Identify and report defects before production release
- Validate automation framework coverage and reliability
- Demonstrate compliance with HR business processes

### 1.3 Scope

**In Scope:**
- Authentication & Authorization module
- Employee Management (PIM)
- Leave Management
- Payroll Processing
- Recruitment & Onboarding
- Reports & Analytics
- Admin Settings

**Out of Scope:**
- Third-party integrations (payroll banking)
- Hardware/biometric device testing
- Disaster recovery testing

---

## 2. Test Strategy

### 2.1 Testing Types

| Testing Type | Tool | Owner | When |
|-------------|------|-------|------|
| Functional Testing | Manual + Selenium | QA Team | Sprint |
| Regression Testing | Selenium (Automated) | QA Automation | Every release |
| API Testing | RestAssured + Postman | QA Automation | Each API change |
| Performance Testing | JMeter | QA Lead | Pre-release |
| Mobile Testing | Appium | QA Mobile | Sprint |
| Security Testing | OWASP ZAP | Security QA | Monthly |
| UAT | Manual | Business Users | Pre-production |

### 2.2 Test Levels

```
┌──────────────────────────────────┐
│         System Testing           │  ← Full HRM system tests
├──────────────────────────────────┤
│      Integration Testing         │  ← Module-to-module flows
├──────────────────────────────────┤
│       Component Testing          │  ← Individual module tests
├──────────────────────────────────┤
│         Unit Testing             │  ← Business logic validation
└──────────────────────────────────┘
```

### 2.3 Entry Criteria
- [ ] All HRM modules deployed to QA environment
- [ ] Test data prepared and loaded
- [ ] Test cases reviewed and approved
- [ ] Test environment verified accessible
- [ ] Automation framework set up and smoke tests passing

### 2.4 Exit Criteria
- [ ] 100% of Critical/High priority test cases executed
- [ ] Zero open S1 (Critical) defects
- [ ] Maximum 5 open S2 (High) defects with workarounds
- [ ] 95%+ pass rate on smoke tests
- [ ] All automated regression tests passing

---

## 3. Test Environment

| Environment | URL | Purpose |
|-------------|-----|---------|
| QA | https://opensource-demo.orangehrmlive.com | Functional testing |
| Staging | [Staging URL] | Pre-production validation |
| Production | [Prod URL] | Smoke tests only (post-deploy) |

### Test Accounts

| Role | Username | Password | Purpose |
|------|----------|----------|---------|
| Admin | Admin | admin123 | Full system access |
| HR Manager | Pam.Job | Pam@123 | HR-level access |
| Employee | Paul.T | Paul@123 | Employee self-service |

---

## 4. Test Schedule

| Phase | Activities | Duration | Owner |
|-------|-----------|---------|-------|
| Phase 1 | Test case preparation & review | Week 1 | QA Lead |
| Phase 2 | Module testing (Auth + Employee) | Week 2-3 | QA Team |
| Phase 3 | Module testing (Leave + Payroll) | Week 4-5 | QA Team |
| Phase 4 | Module testing (Recruitment + Reports) | Week 6 | QA Team |
| Phase 5 | Regression testing | Week 7 | QA Automation |
| Phase 6 | Performance testing | Week 8 | QA Lead |
| Phase 7 | UAT | Week 9-10 | Business Users |
| Phase 8 | Sign-off | Week 10 | QA Lead + PM |

---

## 5. Risk Management

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|-----------|
| Demo environment downtime | Medium | High | Test during off-peak hours |
| Test data corruption | Low | High | Refresh test data daily |
| Flaky tests in automation | Medium | Medium | Retry mechanism (3 attempts) |
| Resource unavailability | Low | Medium | Cross-train team members |
| Scope creep | Medium | High | Change control process |

---

## 6. Defect Management

### 6.1 Severity Classification

| Severity | Definition | Resolution SLA |
|----------|-----------|----------------|
| S1 - Critical | System crash, data loss, security breach | Same day |
| S2 - High | Core functionality broken, no workaround | 24 hours |
| S3 - Medium | Feature works with workaround | 3 days |
| S4 - Low | Minor UI issues, cosmetic | Next sprint |

### 6.2 Bug Report Fields
- Bug ID, Title, Description
- Steps to Reproduce
- Expected vs Actual Result
- Severity & Priority
- Environment, Browser, OS
- Screenshots/Logs
- Assigned To, Status

---

## 7. Deliverables

| Deliverable | Type | Scheduled |
|-------------|------|-----------|
| Test Plan | Document | Week 1 |
| Test Cases (200+) | Excel/Markdown | Week 1-2 |
| Bug Reports | Jira/Template | Ongoing |
| Traceability Matrix | Excel | Week 2 |
| Weekly Status Reports | Email | Weekly |
| Test Summary Report | Document | End of phase |
| Automation Report | HTML | Each run |

---

## 8. Sign-Off

| Role | Name | Signature | Date |
|------|------|-----------|------|
| QA Lead | Deep Ghevariya | — | April 2025 |
| Project Manager | — | — | — |
| Business Analyst | — | — | — |
| Product Owner | — | — | — |
