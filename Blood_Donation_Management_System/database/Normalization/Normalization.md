# Database Normalization – LifeSaver Blood Donation Management System

## 1. Introduction

Database normalization is the process of organizing data into structured tables to reduce data duplication and improve data consistency.

The LifeSaver Blood Donation Management System uses normalization principles to organize user, donor, donation, blood request, and stock information.

## 2. First Normal Form (1NF)

A table is in First Normal Form when:
- Each column contains atomic (single) values.
- Each row is unique.
- There are no repeating groups.

**Application in this project:**

Each user, donor, donation, and blood request is stored as a separate record. Each field contains a single value.

## 3. Second Normal Form (2NF)

A table is in Second Normal Form when:
- It is already in 1NF.
- Every non-key attribute depends on the whole primary key.
- There are no partial dependencies on a composite key.

**Application in this project:**

The project tables use single-column primary keys such as `user_id`, `donor_id`, `request_id`, and `donation_id`. Non-key attributes are associated with their respective records.

## 4. Third Normal Form (3NF)

A table is in Third Normal Form when:
- It is already in 2NF.
- Non-key attributes do not depend on other non-key attributes.

**Application in this project:**

User details are maintained in the Users table, donor details in the Donors table, and donation information in the Donations table. Foreign keys connect related records and reduce unnecessary duplication.

## 5. Benefits of Normalization

1. Reduces unnecessary data duplication.
2. Improves data consistency.
3. Makes data easier to maintain.
4. Helps organize database relationships.
5. Reduces update and deletion anomalies.

## 6. Conclusion

Normalization helps maintain an organized and consistent database for the LifeSaver Blood Donation Management System. The database structure follows 1NF, 2NF, and 3NF design principles.