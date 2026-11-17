# LifeSaver – Blood Donation Management System
## Software Testing and Test Cases

**Version:** 1.0  
**Date:** November 2026

## 1. Introduction

This document describes the test cases prepared for the LifeSaver Blood Donation Management System. Testing is performed to verify the functionality of donor registration, authentication, donor search, blood requests, administrator operations, database connectivity and Python analysis.

## 2. Testing Objectives

- Verify that donor registration works correctly.
- Check login and logout functionality.
- Verify donor search results.
- Test blood request submission and history.
- Check administrator request management.
- Verify blood stock updates.
- Check database connectivity.
- Verify Python analysis output.

## 3. Testing Methods

| Testing Type | Description |
|---|---|
| Functional Testing | Checks whether system features work as expected. |
| Validation Testing | Checks whether invalid inputs are handled correctly. |
| Integration Testing | Checks communication between frontend, backend and database. |
| Database Testing | Checks data insertion, retrieval and updates. |
| Output Testing | Checks whether reports and charts are generated correctly. |

## 4. Test Environment

| Component | Configuration |
|---|---|
| Frontend | HTML, CSS, JavaScript |
| Backend | Java |
| Database | MySQL 8.4 |
| Connectivity | JDBC |
| Analysis | Python 3 |
| Browser | Google Chrome |
| Operating System | Windows |

## 5. Test Cases

**Note:** The Actual Result and Status columns must be completed after executing each test. Do not mark a test as Pass before testing it.

| Test ID | Test Scenario | Test Input / Action | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| TC-01 | Valid donor registration | Enter valid donor details | Account should be created successfully | Pending | Not Tested |
| TC-02 | Duplicate email registration | Register using an existing email | System should display an error | Pending | Not Tested |
| TC-03 | Empty registration fields | Submit without required details | Validation message should appear | Pending | Not Tested |
| TC-04 | Invalid age | Enter an age outside the accepted range | System should reject invalid age | Pending | Not Tested |
| TC-05 | Invalid blood group | Enter an unsupported blood group | System should reject the input | Pending | Not Tested |
| TC-06 | Password storage | Register a donor and inspect the database password field | Password should be stored as a hash | Pending | Not Tested |
| TC-07 | Database connection | Start the application with valid DB configuration | Database connection should succeed | Pending | Not Tested |
| TC-08 | Valid login | Enter correct email and password | User should log in successfully | Pending | Not Tested |
| TC-09 | Invalid login | Enter incorrect password | Login should be rejected | Pending | Not Tested |
| TC-10 | Empty login fields | Submit empty login form | Validation message should appear | Pending | Not Tested |
| TC-11 | Donor access control | Log in as a donor and access admin page | Access should be denied | Pending | Not Tested |
| TC-12 | Admin access | Log in as an administrator | Admin pages should be accessible | Pending | Not Tested |
| TC-13 | Logout | Select logout | Session should be ended | Pending | Not Tested |
| TC-14 | Profile viewing | Open donor profile | Correct profile details should appear | Pending | Not Tested |
| TC-15 | Donor search by blood group | Select a blood group | Matching available donors should appear | Pending | Not Tested |
| TC-16 | Donor search by city | Enter a city | Matching donors should appear | Pending | Not Tested |
| TC-17 | Donor search with no match | Enter criteria with no matching donors | Empty result should be displayed | Pending | Not Tested |
| TC-18 | Valid blood request | Enter valid request details | Request should be saved | Pending | Not Tested |
| TC-19 | Invalid blood request | Submit incomplete or invalid details | System should show a validation message | Pending | Not Tested |
| TC-20 | Request status | Submit a new request | Initial status should be Pending | Pending | Not Tested |
| TC-21 | View request history | Open request history | Own requests should be displayed | Pending | Not Tested |
| TC-22 | Admin request list | Open admin request page | All requests should be displayed | Pending | Not Tested |
| TC-23 | Approve request with sufficient stock | Approve a pending request | Status should update and stock should decrease | Pending | Not Tested |
| TC-24 | Approve request with insufficient stock | Approve a request exceeding available stock | Approval should be rejected | Pending | Not Tested |
| TC-25 | Reject pending request | Select Reject | Request status should update to Rejected | Pending | Not Tested |
| TC-26 | Update blood stock | Enter a valid stock quantity | Stock value should be updated | Pending | Not Tested |
| TC-27 | Invalid stock update | Enter a negative quantity | System should reject invalid quantity | Pending | Not Tested |
| TC-28 | Donation record | Run the donation console utility with valid details | Donation record should be saved | Pending | Not Tested |
| TC-29 | Donation stock update | Record a completed donation | Stock should increase according to implemented logic | Pending | Not Tested |
| TC-30 | Python analysis | Execute analysis.py with valid database configuration | Analysis report should be generated | Pending | Not Tested |
| TC-31 | Python chart output | Open generated HTML report | Stock and donation charts should be displayed | Pending | Not Tested |

## 6. Test Execution Summary

Complete this table after executing the test cases.

| Item | Count |
|---|---:|
| Total Test Cases | 31 |
| Passed | To be filled |
| Failed | To be filled |
| Not Tested | To be filled |

## 7. Defect Reporting

Any failed test should be recorded with the following details:

| Field | Description |
|---|---|
| Defect ID | Unique defect number |
| Test Case ID | Related test case |
| Issue Description | Details of the problem |
| Expected Result | Correct expected behaviour |
| Actual Result | Observed behaviour |
| Status | Open / Fixed / Retested |

## 8. Conclusion

The test cases listed in this document are designed to verify the main features of the LifeSaver Blood Donation Management System. Actual test results will be recorded after executing the application and checking its outputs.