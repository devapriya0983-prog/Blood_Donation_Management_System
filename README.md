LifeSaver -- Blood Donation Management System
Project Description
LifeSaver is a web-based Blood Donation Management System developed as
an individual Project-Based Assessment for the Department of Information
Technology.

The project focuses on maintaining donor information, finding available
donors, submitting and tracking blood requests, managing blood stock,
recording donation details, and analysing blood-stock and donation data.

The application uses a Java backend, an HTML/CSS/JavaScript frontend, a
MySQL database accessed through JDBC, and Python for data retrieval and
visualization.
Implementation note: This README describes the project structure
and functionality identified in the supplied project files. A listed
page or class does not by itself prove that the feature has been fully
tested. Refer to the testing document and update feature status after
executing the test cases.

Scenario
Scenario 15: Blood Donation Management and Donor Availability Analysis
System

The system is intended to help a blood bank maintain donor records,
monitor blood stock, record donations, receive hospital blood requests,
and analyse donation patterns.
Technologies Used
  Component                Technology
  Frontend                 HTML, CSS, JavaScript
Backend                  Java
Web server               Java HTTP Server (com.sun.net.httpserver)
Database                 MySQL
Database connectivity    JDBC / MySQL Connector/J
Data analysis            Python
Python database access   mysql-connector-python
Visualization            Python chart generation and HTML output
Main Modules
Donor and User Module
- Donor registration
- User login and logout
- Donor profile
- Donor availability information
- Search for available donors by blood group and city
Blood Request Module
- Submit a blood request
- View the logged-in user's request history
- Track request status
- Admin approval or rejection of pending requests
Admin Module
- View blood requests
- Approve or reject pending requests
- View blood stock
- Update blood stock
- View donor information
Donation and Reports Utilities
The Java source includes console-oriented utilities for recording
donations, identifying donors according to a date-based eligibility
rule, and generating reports such as stock availability and donation
history.
Python Data Analysis
The Python analysis script retrieves blood-stock and donation-count data
from MySQL, performs basic processing, and generates chart output in an
HTML report.
Project Folder Structure
The supplied project contains the following main areas. The exact
contents may vary as development continues.
LifeSaver/
├── backend/
│   ├── lib/
│   ├── src/
│   │   ├── BloodRequest.java
│   │   ├── BloodRequestStatus.java
│   │   ├── DBConnection.java
│   │   ├── DonationRecord.java
│   │   ├── DonorAvailability.java
│   │   ├── DonorRegistration.java
│   │   ├── Reports.java
│   │   └── WebServer.java
│   └── bin/
├── database/
│   ├── Database_SQL/
│   └── Diagrams/
├── frontend/
│   ├── css/
│   ├── images/
│   ├── js/
│   ├── about.html
│   ├── admin-dashboard.html
│   ├── admin-donors.html
│   ├── admin-requests.html
│   ├── admin-stock.html
│   ├── blood-request.html
│   ├── contact.html
│   ├── dashboard.html
│   ├── find-donors.html
│   ├── index.html
│   ├── login.html
│   ├── profile.html
│   └── request-history.html
├── Python_Analysis/
│   └── analysis.py
├── SRS/
├── Testing/
└── README.md
Database
The Java and Python source files refer to the following MySQL tables:
- users
- donors
- blood_requests
- donations
- blood_stock
The database name and connection URL must match the configuration in
DBConnection.java and the Python analysis script.

Important schema note: The original SQL creation script was not
present in the supplied project ZIP. Table and column details should
therefore be verified against the actual database before using any
reconstructed SQL schema. To inspect the live table definitions, run the
following commands in your MySQL client:
SHOW CREATE TABLE users;
SHOW CREATE TABLE donors;
SHOW CREATE TABLE blood_requests;
SHOW CREATE TABLE donations;
SHOW CREATE TABLE blood_stock;
Do not assume that a reconstructed schema is the authoritative
production schema.
Database Setup
1. Install and start MySQL Server.
2. Create or select the database configured in DBConnection.java.
3. Verify the actual table definitions using SHOW CREATE TABLE.
4. Ensure the five tables listed above exist and that their columns
   match the SQL statements used by the Java application.
5. Add the MySQL Connector/J JAR file to backend/lib/.
6. Configure database connection details securely.
Database Credentials and Security
Never commit database passwords, API keys, or other secrets to GitHub.

If the Java connection class has been configured to read the password
from an environment variable named DB_PASSWORD, set that variable in
the same terminal session before starting the server.

Example for Windows PowerShell:
$env:DB_PASSWORD = Read-Host "Enter database password"
This is a simple session-only example. Do not paste the password into
source code or commit it to the repository. Configure the Python script
to read credentials securely as well; remove any hard-coded credentials
before publishing.
Build and Run the Java Application
The following commands assume that the project root contains the
backend and frontend folders and that the MySQL Connector/J JAR is
inside backend/lib/.

Open PowerShell in the project root directory.
1. Compile the Java source files
javac -cp "backend\lib\*" -d backend\bin backend\src\*.java
2. Set the database password
If DBConnection.java reads the password from DB_PASSWORD, set it in
the current PowerShell session:
$env:DB_PASSWORD = Read-Host "Enter database password"
3. Start the Java web server
java -cp "backend\bin;backend\lib\*" WebServer
The server source configuration determines the listening port. In the
supplied development setup, the server has been run on port 8081.
4. Open the website
Open a web browser and visit:
http://localhost:8081/
If the application uses a different port or frontend directory
configuration, follow the values defined in WebServer.java and the
project's current run configuration.
Run the Python Data Analysis
The Python analysis script is located at:
Python_Analysis/analysis.py
Install the required Python MySQL connector if it is not already
installed:
python -m pip install mysql-connector-python
Configure the database connection securely in the script or through
environment variables, then run:
python Python_Analysis/analysis.py
The supplied script is intended to retrieve blood-stock and
donation-count information and generate an HTML visualization report.
The output file and location depend on the script configuration. Confirm
that the script executes successfully and that the generated charts
display correctly before submission.
Functional Scope from Scenario 15
The assessment scenario specifies the following requirements:
1. Maintain donor details, including donor ID, name, age, contact
   information, and blood group.
2. Record donation details, including donation ID, donor ID, donation
   date, and number of units donated.
3. Maintain blood stock by blood group and update stock after donations
   and blood-unit issuance.
4. Record hospital blood requests and track their status.
5. Identify donors whose last recorded donation date meets a predefined
   eligibility interval.
6. Generate blood-stock, donation-history, and blood-group-wise
   donation-count reports.
7. Retrieve donation and stock data using Python, preprocess it, and
   generate visualizations.
Implementation status must be checked against the current source
code. In particular, verify blood-unit issuance and the web-based
donation workflow before describing them as implemented. The supplied
source describes donation recording as a Java console utility; do not
assume a corresponding website page exists.

The date-based donor eligibility rule in the supplied Java utility is an
informational software rule, not medical clearance. Actual donation
eligibility must be determined by qualified healthcare personnel and
applicable blood-bank guidelines.
Testing
Test the application after configuring the database. Suggested areas
include:
- Donor registration with valid and invalid inputs
- Login with valid and invalid credentials
- Session and logout behaviour
- Donor profile retrieval
- Donor search by blood group and city
- Blood request submission and request history
- Admin access control
- Request approval and rejection
- Stock update and stock deduction
- Donation recording and stock update
- Python database retrieval and chart generation
Record the actual result only after executing each test. Mark tests that
have not been executed as Not Tested or Pending. Do not report
unverified test cases as passed.
GitHub Submission Checklist
Before pushing the project to GitHub:
- [ ] Include the Java source code.
- [ ] Include the frontend HTML, CSS, and JavaScript files.
- [ ] Include the SQL scripts that match the actual database schema.
- [ ] Include the Python analysis file or notebook.
- [ ] Include generated visualization outputs or representative
  screenshots.
- [ ] Include the SRS, diagrams, database documentation, and testing
  documents.
- [ ] Include this README.md file.
- [ ] Remove database passwords and all private credentials.
- [ ] Check that no .env file or secret configuration is committed.
- [ ] Verify that the repository is accessible to the faculty members.
Limitations and Items to Verify
- The original database SQL creation script was not included in the
  supplied ZIP; verify the live schema before submission.
- Confirm that the configured database name, host, port, SSL settings,
  and credentials match the actual database environment.
- Confirm that blood-unit issuance is implemented and tested; it is a
  requirement in Scenario 15.
- Confirm whether donation recording is available through the web
  interface or only through the Java console utility.
- Execute the Python script and retain genuine output screenshots.
- Execute the testing document's test cases and fill in actual
  results.
Academic Information
- College: Velalar College of Engineering and Technology, Erode --
  12
- Department: Information Technology
- Academic Year: 2026--27 (Odd Semester)
- Class: 25IT3A & B
- Activity: Project-Based Assessment
- Project: LifeSaver -- Blood Donation Management and Donor
  Availability Analysis System
- Student Name: \(Enter your name\)
- Register Number: \(Enter your register number\)
- Faculty / Guide: \(Enter faculty or guide name, if required\)
Note: This project is prepared for academic assessment. It is not a
substitute for professional blood-bank management procedures, medical
screening, or clinical eligibility decisions.