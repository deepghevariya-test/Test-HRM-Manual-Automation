<div align="center">

# 🏢 HRM Testing Automation Framework

[![Selenium](https://img.shields.io/badge/Selenium-4.x-43B02A?style=for-the-badge&logo=selenium&logoColor=white)](https://www.selenium.dev/)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![TestNG](https://img.shields.io/badge/TestNG-7.x-FF6C37?style=for-the-badge&logo=testng&logoColor=white)](https://testng.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Appium](https://img.shields.io/badge/Appium-2.x-662D91?style=for-the-badge&logo=appium&logoColor=white)](https://appium.io/)
[![JMeter](https://img.shields.io/badge/JMeter-5.x-D22128?style=for-the-badge&logo=apachejmeter&logoColor=white)](https://jmeter.apache.org/)
[![Jenkins](https://img.shields.io/badge/Jenkins-CI/CD-D24939?style=for-the-badge&logo=jenkins&logoColor=white)](https://www.jenkins.io/)
[![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-Enabled-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)](https://github.com/features/actions)
[![RestAssured](https://img.shields.io/badge/RestAssured-5.x-5C4EE5?style=for-the-badge)](https://rest-assured.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

**A production-ready, interview-grade testing framework demonstrating full-stack QA expertise**

*Manual Testing • Selenium Automation • API Testing • Mobile Testing • Performance Testing • CI/CD*

[🚀 Quick Start](#quick-start) • [📊 Coverage](#test-coverage) • [🏗️ Architecture](#architecture) • [📁 Structure](#directory-structure)

</div>

---

## 🎯 Project Overview

This repository demonstrates **end-to-end QA expertise** for a Human Resource Management (HRM) system covering both **Manual** and **Automation** testing across all layers of the testing pyramid.

| Layer | Framework | Tests | Status |
|-------|-----------|-------|--------|
| UI Automation | Selenium 4 + TestNG | 50+ tests | ✅ Active |
| API Testing | RestAssured 5 + Postman | 30+ tests | ✅ Active |
| Mobile Testing | Appium 2 | 15+ tests | ✅ Active |
| Performance | JMeter 5 | 5 test plans | ✅ Active |
| Manual Testing | Markdown + Templates | 200+ cases | ✅ Active |
| CI/CD | Jenkins + GitHub Actions | 3 pipelines | ✅ Active |

**Target Application:** [OrangeHRM Open Source](https://opensource-demo.orangehrmlive.com/)

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    CI/CD PIPELINE (Jenkins / GitHub Actions)     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐   │
│  │ Checkout │→ │  Build   │→ │  Tests   │→ │   Reports    │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────┘   │
└─────────────────────────────────────────────────────────────────┘
         │                          │
         ▼                          ▼
┌─────────────────┐    ┌────────────────────────────────────────┐
│   TEST LAYERS   │    │              HRM APPLICATION            │
│                 │    │                                        │
│ ┌─────────────┐ │    │  ┌──────────┐  ┌──────────────────┐  │
│ │  Selenium   │─┼────┼→ │  Web UI  │  │   REST API Layer │  │
│ │  (Web UI)   │ │    │  └──────────┘  └──────────────────┘  │
│ └─────────────┘ │    │       │                  │            │
│ ┌─────────────┐ │    │  ┌────▼─────────────────▼──────────┐ │
│ │Rest Assured │─┼────┼→ │           Business Logic         │ │
│ │   (API)     │ │    │  └─────────────────────────────────┘ │
│ └─────────────┘ │    │       │                              │
│ ┌─────────────┐ │    │  ┌────▼─────────────────────────┐   │
│ │   Appium    │─┼────┼→ │         Database Layer        │   │
│ │  (Mobile)   │ │    │  └──────────────────────────────┘   │
│ └─────────────┘ │    └────────────────────────────────────────┘
│ ┌─────────────┐ │
│ │   JMeter    │ │    ┌────────────────────────────────────────┐
│ │(Performance)│ │    │         REPORTING SUITE                 │
│ └─────────────┘ │    │  ExtentReports │ Allure │ JMeter HTML  │
└─────────────────┘    └────────────────────────────────────────┘
```

### Framework Design Patterns
- **Page Object Model (POM)** with Page Factory for Selenium
- **Builder Pattern** for API request construction (RestAssured)
- **Data-Driven Testing** (Apache POI + CSV + JSON)
- **BDD-style** API tests (given/when/then)
- **Retry Mechanism** for flaky test handling
- **Factory Pattern** for cross-browser WebDriver creation

---

## 📊 Test Coverage

| Module | Manual | Selenium | API | Mobile |
|--------|--------|----------|-----|--------|
| Authentication | 20 | 10 | 5 | 3 |
| Employee Management | 50 | 15 | 8 | 3 |
| Leave Management | 30 | 10 | 5 | 2 |
| Payroll | 40 | 8 | 5 | 2 |
| Recruitment | 25 | 8 | 4 | 2 |
| Reports & Analytics | 15 | 0 | 3 | 1 |
| Admin Settings | 20 | 0 | 0 | 0 |
| **TOTAL** | **200+** | **51+** | **30+** | **13+** |

---

## 🛠️ Tech Stack

| Category | Technology | Version |
|----------|-----------|---------|
| Language | Java | 17 LTS |
| Build Tool | Maven | 3.9+ |
| UI Automation | Selenium WebDriver | 4.x |
| Test Framework | TestNG | 7.x |
| Driver Management | WebDriverManager | 5.x |
| API Testing | RestAssured | 5.x |
| API Docs/Testing | Postman + Newman | Latest |
| Mobile | Appium | 2.x |
| Performance | Apache JMeter | 5.x |
| Reports | ExtentReports 5 + Allure | Latest |
| Logging | Log4j2 | 2.x |
| Data-Driven | Apache POI (Excel) | 5.x |
| JSON | Jackson + Gson | 2.x |
| CI/CD | Jenkins + GitHub Actions | Latest |
| Containers | Docker + Docker Compose | Latest |

---

## ⚡ Quick Start

### Prerequisites
```bash
# Required installations
Java 17+      → https://adoptium.net/
Maven 3.9+    → https://maven.apache.org/download.cgi
Chrome/Firefox/Edge  → Latest versions
Git           → https://git-scm.com/

# Optional
JMeter 5.x   → https://jmeter.apache.org/download_jmeter.cgi
Appium 2.x   → npm install -g appium
Node.js 18+  → https://nodejs.org/ (for Newman/Postman CLI)
Docker        → https://docs.docker.com/get-docker/
```

### Clone & Setup
```bash
git clone https://github.com/deepghevariya/HRM-Testing-Automation-Framework.git
cd HRM-Testing-Automation-Framework

# Selenium Automation
cd 01-Selenium-Automation
cp src/test/resources/config.properties.example src/test/resources/config.properties
# Edit config.properties with your settings
mvn clean install -DskipTests
```

### 🧪 Execution Commands

```bash
# ═══════════ SELENIUM TESTS ═══════════
# Smoke Tests (10 critical tests)
mvn test -Dsuite=smoke-test

# Full Regression Suite (50+ tests)
mvn test -Dsuite=regression-suite

# Cross-Browser Tests
mvn test -Dsuite=cross-browser -Dbrowser=firefox
mvn test -Dsuite=cross-browser -Dbrowser=edge

# Parallel Execution (4 threads)
mvn test -Dsuite=parallel-execution

# Headless Mode
mvn test -Dheadless=true

# ═══════════ API TESTS ═══════════
cd 02-API-Testing/RestAssured_Java
mvn test -Dgroups=api

# Newman (Postman CLI)
cd 02-API-Testing
newman run Postman_Collections/HRM_API_Collection.json \
  -e Postman_Collections/HRM_Environment.json \
  --reporters cli,html \
  --reporter-html-export Newman_Reports/report.html

# ═══════════ PERFORMANCE TESTS ═══════════
cd 04-Performance-Testing-JMeter
jmeter -n -t TestPlans/HRM_LoadTest_100Users.jmx \
  -l Reports/results.jtl \
  -e -o Reports/dashboard/

# ═══════════ MOBILE TESTS ═══════════
# Start Appium server first
appium &
cd 03-Mobile-Testing-Appium
mvn test -Dplatform=android
```

---

## 📁 Directory Structure

```
HRM-Testing-Automation-Framework/
│
├── 📁 01-Selenium-Automation/          → Web UI Automation (Java/TestNG/POM)
├── 📁 02-API-Testing/                  → REST API Tests (RestAssured + Postman)
├── 📁 03-Mobile-Testing-Appium/        → Mobile Tests (Android + iOS)
├── 📁 04-Performance-Testing-JMeter/   → Load/Stress/Spike/Endurance Tests
├── 📁 05-Manual-Testing-Documentation/ → 200+ Test Cases, Bug Reports, RTM
├── 📁 06-CI-CD-Jenkins-Pipeline/       → Jenkins + Docker + GitHub Actions
├── 📁 07-Resources-Templates/          → Reusable templates
├── 📁 docs/                            → Architecture, Setup, Coverage docs
├── .gitignore
├── LICENSE                             → MIT License
└── MAIN_README.md                      → This file
```

---

## 📈 Sample Reports

> After running tests, HTML reports are auto-generated in:
> - `01-Selenium-Automation/reports/ExtentReport.html`
> - `02-API-Testing/Newman_Reports/report.html`
> - `04-Performance-Testing-JMeter/Reports/dashboard/index.html`

---

## 🧪 HRM Modules Covered

1. **Authentication & Authorization** — Login, RBAC, 2FA, session management
2. **Employee Management** — CRUD, bulk import, document upload, search/filter
3. **Attendance & Time Tracking** — Check-in/out, timesheets, overtime
4. **Leave Management** — Application workflow, approval hierarchy, balance
5. **Payroll Processing** — Salary structure, deductions, payslip generation
6. **Recruitment & Onboarding** — Job postings, pipeline, offer letters
7. **Performance Management** — KPI/KRA, appraisal cycles, 360° feedback
8. **Reports & Analytics** — Headcount, attendance, payroll, custom exports

---

## 🤝 Contributing

See [CONTRIBUTING.md](docs/CONTRIBUTING.md) for guidelines.

```bash
# Fork → Branch → Test → PR
git checkout -b feature/your-test-name
git commit -m "feat: add test for [module]"
git push origin feature/your-test-name
```

---

## 📧 Contact

**Deep Ghevariya** — QA Engineer  
📧 deepghevariya8890@gmail.com  
🐙 [GitHub](https://github.com/deepghevariya)  
💼 1+ Year at Seravion Technologies | ERP/CRM/HRM Testing Specialist

---

<div align="center">

⭐ **If this project helped you, please give it a star!** ⭐

*Built with  to demonstrate real-world QA expertise*
" all done "
</div>
