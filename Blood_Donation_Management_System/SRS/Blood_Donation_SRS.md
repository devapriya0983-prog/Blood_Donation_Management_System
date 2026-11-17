# LifeSaver – Blood Donation Management System
## Software Requirements Specification (SRS)

**Version:** 1.0  
**Date:** November 2026  
**Project Scenario:** Scenario 15 – Blood Donation Management and Donor Availability Analysis System

### Student Details

- **Student Name:** Devapriya M
- **Register Number:** 7329265ITR015
- **Department / Class:** B.Tech Information Technology / 25IT3A
- **College:** Velalar College of Engineering and Technology

---

# Document Control

| Item | Details |
|---|---|
| Project | LifeSaver – Blood Donation Management System |
| Document | Software Requirements Specification |
| Version | 1.0 |
| Prepared By | [Devapriya M / 7329265ITR015] |
| Approval | Subject to college approval |
| Technology | Java, HTML, CSS, JavaScript, MySQL, Python |

# Table of Contents

1. Introduction  
2. Overall Description  
3. Functional Requirements  
4. Non-Functional Requirements  
5. External Interface and System Requirements  
6. Use Case Descriptions  
7. Constraints, Assumptions and Known Issues  
8. Requirement Traceability  

---

# 1. Introduction

## 1.1 Purpose

This Software Requirements Specification describes the functional and non-functional requirements of the LifeSaver Blood Donation Management System. It explains the system's purpose, users, features, interfaces, database requirements and limitations.

This document is intended for project guides, evaluators, developers and testers.

## 1.2 Scope

LifeSaver is a web-based Blood Donation Management System designed to manage donor registration, donor availability, blood requests and blood stock information.

The system provides the following major features:

- Donor registration and login.
- Secure password storage.
- Donor profile management.
- Searching for available donors by blood group and city.
- Submitting blood requests.
- Viewing personal request history.
- Admin management of blood requests.
- Blood stock viewing and updating.
- Donation record management through Java console utilities.
- Donor availability analysis.
- Python-based database analysis and visualisation.

The application uses Java for backend processing, HTML,CSS and JavaScript for the frontend, and MySQL for database storage. Python is used to retrieve and analyse database information and generate visual reports.

The system does not provide medical advice or make final medical eligibility decisions. Donor eligibility must be confirmed by qualified healthcare professionals.

## 1.3 Definitions and Abbreviations

Term

Meaning

API

Application Programming Interface

JDBC

Java Database Connectivity

PBKDF2

Password-Based Key Derivation Function 2

SQL

Structured Query Language

DBMS

Database Management System

Donor

A registered person who may donate blood

Admin

User who manages system operations

Blood Stock

Available units of blood grouped by blood type

HTTP

Hypertext Transfer Protocol

## 1.4 Problem Statement

Traditional blood donation management often depends on manual registers, telephone calls and spreadsheets. These methods can make donor identification, blood request processing and stock tracking slow and difficult.

A centralised digital system is required to maintain donor information, search for available donors, manage blood requests and monitor blood stock efficiently.

LifeSaver aims to simplify these operations through a web-based application connected to a database.

# 2. Overall Description

## 2.1 Existing System

In the existing manual process:

Donor information is maintained in registers or spreadsheets.

Blood requests are received through phone calls.

Donors are contacted individually.

Blood stock is checked manually.

Maintaining and retrieving historical records can be difficult.

These methods may result in delays, duplicate records and errors.

## 2.2 Proposed System

The proposed LifeSaver system provides:

Online donor registration.

Login and role-based access for donors and administrators.

Donor search based on blood group and city.

Online blood request submission.

Request approval and rejection by administrators.

Blood stock viewing and updating.

Donation record management through Java console utilities.

Donor availability analysis.

Python-based reports and charts.

## 2.3 Product Perspective

The application follows a three-tier architecture.

Presentation Layer: HTML, CSS and JavaScript pages displayed in a web browser.

Application Layer: Java backend responsible for request processing, authentication, validation and business logic.

Database Layer: MySQL database accessed using JDBC and MySQL Connector/J.

Python connects to the database separately to retrieve data, perform preprocessing and generate visualisations.

Refer to database/Diagrams/System_Architecture.drawio for the system architecture diagram.

## 2.4 User Roles

Role

Description

Main Capabilities

Guest

Visitor who has not logged in

View public pages, register and log in

Donor

Registered user

View dashboard and profile, search donors, submit blood requests and view request history

Admin

Authorised administrator

View donors, manage requests, approve or reject requests and manage blood stock

## 2.5 Feature Status

The following status descriptions are used:

Implemented: Source code for the feature is present.

Implemented and tested: The feature has been executed and its test results recorded.

Planned: The feature is proposed but not yet implemented.

Feature

Status

Donor registration

Implemented; testing to be recorded

Login and logout

Implemented; testing to be recorded

Password hashing

Implemented

Donor profile

Implemented; testing to be recorded

Donor search

Implemented; testing to be recorded

Blood request submission

Implemented; testing to be recorded

Request history

Implemented; testing to be recorded

Admin request management

Implemented; testing to be recorded

Admin stock management

Implemented; testing to be recorded

Admin donor list

Implemented; testing to be recorded

Donation recording through console

Implemented in Java source; testing to be recorded

Donor availability utility

Implemented in Java source; testing to be recorded

Console reports

Implemented in Java source; testing to be recorded

Python stock and donation charts

Implemented in Python source; testing to be recorded

Monthly donation trend analysis

Planned unless separately implemented

Web-based donation recording

Planned unless separately implemented

# 3. Functional Requirements

ID

Area

Requirement

Priority

FR-01

Registration

The system shall allow donors to register with their personal details, contact information, age and blood group.

High

FR-02

Database

The system shall store user and donor information in the MySQL database.

High

FR-03

Security

The system shall store passwords as secure password hashes instead of plain text.

High

FR-04

Login

The system shall authenticate users using their registered email and password.

High

FR-05

Access Control

The system shall provide separate access permissions for donors and administrators.

High

FR-06

Logout

The system shall allow logged-in users to log out securely.

Medium

FR-07

Profile

The system shall display the profile information of the logged-in donor.

Medium

FR-08

Donor Search

The system shall allow users to search available donors using blood group and city.

High

FR-09

Blood Request

The system shall allow logged-in users to submit blood requests.

High

FR-10

Request History

The system shall allow users to view their own blood request history.

Medium

FR-11

Admin Requests

The system shall allow administrators to view all blood requests.

High

FR-12

Request Approval

The system shall allow administrators to approve or reject pending blood requests.

High

FR-13

Stock Validation

The system shall check available blood stock before approving a request.

High

FR-14

Stock Management

The system shall allow administrators to view and update available blood units.

High

FR-15

Donor Management

The system shall allow administrators to view registered donor information.

Medium

FR-16

Donation Records

The Java donation utility shall store donation records in the database.

Medium

FR-17

Availability Analysis

The system shall identify donors based on the configured donation-date interval.

Medium

FR-18

Reports

The system shall generate reports about blood stock, donation history and blood-group-wise donation counts.

Medium

FR-19

Python Analysis

Python shall retrieve and preprocess database information for analysis.

Medium

FR-20

Visualisation

Python shall generate charts representing blood stock and donation counts.

Medium

# 4. Non-Functional Requirements

ID

Category

Requirement

NFR-01

Security

Passwords shall be stored using secure hashing. Server-side role checks shall protect restricted operations.

NFR-02

Data Integrity

Database operations involving multiple changes shall use transactions where implemented.

NFR-03

Usability

The website shall provide simple navigation and understandable form messages.

NFR-04

Performance

The application shall support normal usage in a small institutional environment.

NFR-05

Reliability

The application shall handle database and validation errors without terminating unexpectedly.

NFR-06

Portability

The application shall be usable on supported operating systems with the required Java, MySQL and Python environments.

NFR-07

Maintainability

Backend modules, frontend pages, database scripts and Python analysis shall be maintained separately.

NFR-08

Privacy

Donor information shall be accessed according to user roles and the application's access rules.

# 5. External Interface and System Requirements

## 5.1 Hardware Requirements

Component

Minimum Requirement

Processor

Dual-core processor

RAM

4 GB

Storage

500 MB free space

Network

Internet or local network connection

These are proposed minimum project requirements, not measured performance results.

## 5.2 Software Requirements

Component

Technology

Operating System

Windows / Linux / macOS

Frontend

HTML, CSS, JavaScript

Backend

Java

Database

MySQL 8.4

Database Connectivity

JDBC

JDBC Driver

MySQL Connector/J

Data Analysis

Python 3

Python Database Library

mysql-connector-python

Browser

Chrome, Edge or Firefox

Development Environment

Visual Studio Code

## 5.3 Database Configuration

The project uses a MySQL database hosted through a cloud database service.

Item

Configuration

Database Type

MySQL

Database Name

main

Host

lifesaver-db-09vc1e.db.enzonic.com

Port

3306

Database Access

JDBC

Password Configuration

Environment variable DB_PASSWORD

The database password must not be stored directly in source code or committed to GitHub.

The Java backend and Python analysis script must use the same database configuration.

## 5.4 Database Tables

The database contains the following main tables:

Table

Purpose

users

Stores user login and account information

donors

Stores donor details and availability

blood_requests

Stores blood request information and status

donations

Stores donation history

blood_stock

Stores available blood units for each blood group

## 5.5 External Interfaces

The web application communicates with the Java backend through HTTP endpoints.

The following endpoints represent the API operations included in the application:

Method

Endpoint

Purpose

POST

/api/register

Register a donor

POST

/api/login

Authenticate a user

POST

/api/logout

Log out a user

GET

/api/profile

Retrieve profile information

GET

/api/donors

Search available donors

POST

/api/blood-request

Submit a blood request

GET

/api/my-blood-requests

View personal request history

GET

/api/admin/check

Check administrator access

GET

/api/admin/requests

View all blood requests

POST

/api/admin/request-status

Approve or reject a request

GET

/api/admin/stock

View blood stock

POST

/api/admin/update-stock

Update blood stock

The exact endpoint behaviour and access permissions are determined by the Java backend implementation.

6. Use Case Descriptions

UC-01 – Register as Donor

Actor: Guest

Precondition: The guest has access to the registration page.

Main Flow:

Guest opens the registration page.

Guest enters the required personal details.

System validates the submitted information.

System securely processes the password.

System stores user and donor information in the database.

Postcondition: Donor account is created successfully.

Alternative Flow: Invalid information or an already registered email results in an error message.

UC-02 – Login

Actor: Donor / Admin

Precondition: User account exists.

Main Flow:

User enters email and password.

System verifies the login information.

System creates an authenticated session.

User is redirected according to their role.

Postcondition: User is logged in.

Alternative Flow: Invalid credentials result in an authentication error.

UC-03 – Logout

Actor: Donor / Admin

Precondition: User is logged in.

Main Flow:

User selects logout.

System invalidates the session.

User returns to the public pages.

Postcondition: User is logged out.

UC-04 – View Profile

Actor: Donor

Precondition: Donor is logged in.

Main Flow:

Donor opens the profile page.

System retrieves the donor's details.

System displays the profile.

Postcondition: Donor profile is displayed.

UC-05 – Find Available Donors

Actor: Donor / Admin

Precondition: User is logged in.

Main Flow:

User selects a blood group and/or enters a city.

System searches available donor records.

Matching donor information is displayed.

Postcondition: Matching donor details are displayed.

UC-06 – Submit Blood Request

Actor: Donor

Precondition: User is logged in.

Main Flow:

User opens the blood request page.

User enters patient and hospital details.

User selects the required blood group and units.

System validates the request.

System stores the request with its initial status.

Postcondition: Blood request is saved.

UC-07 – View Request History

Actor: Donor

Precondition: User is logged in.

Main Flow:

Donor opens the request history page.

System retrieves requests associated with the logged-in user.

System displays the request details and status.

Postcondition: Personal request history is displayed.

UC-08 – View All Blood Requests

Actor: Admin

Precondition: Admin is authenticated.

Main Flow:

Admin opens the request management page.

System retrieves all blood requests.

Requests are displayed for review.

Postcondition: All requests are available for administration.

UC-09 – Approve or Reject Blood Request

Actor: Admin

Precondition: Admin is authenticated and a pending request exists.

Main Flow:

Admin selects a request.

Admin chooses approve or reject.

For approval, the system checks available stock.

If sufficient stock exists, the system updates the request status and stock.

System displays the result.

Postcondition: Request status is updated.

Alternative Flow: If stock is insufficient, approval is not completed.

UC-10 – View and Update Blood Stock

Actor: Admin

Precondition: Admin is authenticated.

Main Flow:

Admin opens the stock management page.

System displays blood groups and available units.

Admin enters the updated stock quantity.

System validates and saves the update.

Postcondition: Blood stock information is updated.

UC-11 – View Donor List

Actor: Admin

Precondition: Admin is authenticated.

Main Flow:

Admin opens the donor management page.

System retrieves donor information.

System displays the donor list.

Postcondition: Donor list is displayed.

UC-12 – Record Donation

Actor: Admin / Operator

Precondition: Database connection is available.

Main Flow:

Operator opens the Java console utility.

Operator enters the donor ID and donation details.

System validates the information.

System stores the donation record.

Stock is updated according to the implemented donation status logic.

Postcondition: Donation record is saved.

UC-13 – Identify Eligible Donors

Actor: Admin / Operator

Precondition: Donor and donation records are available.

Main Flow:

Operator runs the donor availability utility.

System checks previous completed donation dates.

System displays donors matching the configured date rule.

Postcondition: A list of matching donors is displayed.

Note: The configured 90-day interval is a software rule only. It does not establish medical eligibility.

UC-14 – Generate Reports

Actor: Admin / Operator

Precondition: Database connection is available.

Main Flow:

Operator runs the Java reports utility.

Operator selects a report.

System retrieves the required database records.

System displays the report.

Postcondition: Requested report is generated.

UC-15 – Generate Python Visualisations

Actor: Admin / Project Operator

Precondition: Python environment and database connection are configured.

Main Flow:

Operator executes the Python analysis script.

Python retrieves stock and donation information.

Data is preprocessed.

Charts are generated.

An HTML analysis report is produced.

Postcondition: Python analysis output is generated.

# 7. Constraints, Assumptions and Known Issues

## 7.1 Constraints

The system requires a working Java environment.

MySQL database access must be configured correctly.

The MySQL Connector/J library must be available.

Python database connectivity must be configured for analysis.

The system supports the eight standard blood groups: A+, A-, B+, B-, O+, O-, AB+ and AB-.

The application depends on network connectivity to access the cloud database.

## 7.2 Assumptions

Required database tables are created before application execution.

Initial blood stock records are available for the supported blood groups.

An administrator account is configured separately.

Users have access to a modern web browser.

Database credentials are supplied through secure environment configuration.

## 7.3 Known Issues and Limitations

Database configuration: The Java backend and Python analysis script must use the same cloud database settings. Any remaining local database configuration must be corrected before final deployment.

Credentials: Database passwords must not be hard-coded in Java or Python source files. Existing exposed credentials should be changed and replaced with environment-based configuration.

Session management: Sessions stored in application memory may be lost when the server restarts.

Console utilities: Donation recording, donor availability and reports are available through Java console utilities and are not necessarily accessible through web pages.

Python analysis: Only the charts actually generated by the current Python script should be reported as implemented.

Medical eligibility: The system's date-based donor availability rule is not a substitute for medical screening.

Testing: A feature should be marked as tested only after its test case has been executed and the actual result recorded.

Database operations: Stock updates and donation recording must be checked to ensure that database records remain consistent.

# 8. Requirement Traceability

Requirement

Related Use Case

Main Implementation

FR-01 to FR-03

UC-01

DonorRegistration.java

FR-04 to FR-06

UC-02, UC-03

WebServer.java

FR-07

UC-04

WebServer.java

FR-08

UC-05

WebServer.java, DonorAvailability.java

FR-09 to FR-10

UC-06, UC-07

WebServer.java, BloodRequest.java

FR-11 to FR-13

UC-08, UC-09

WebServer.java, BloodRequestStatus.java

FR-14

UC-10

WebServer.java

FR-15

UC-11

WebServer.java

FR-16

UC-12

DonationRecord.java

FR-17

UC-13

DonorAvailability.java

FR-18

UC-14

Reports.java

FR-19 to FR-20

UC-15

Python_Analysis/analysis.py

# 9. Conclusion

The LifeSaver Blood Donation Management System provides a centralised platform for donor registration, donor search, blood request processing and blood stock management. It combines Java backend processing, a web-based frontend, MySQL database storage and Python-based data analysis.

The system aims to reduce manual work, improve access to donor information and support better blood stock monitoring. Future improvements can include additional analytical reports, enhanced session management and web-based donation management.

End of Software Requirements Specification