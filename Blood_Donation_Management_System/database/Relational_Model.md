# Relational Model – LifeSaver Blood Donation Management System

## 1. Introduction

The relational model represents the database structure of the LifeSaver Blood Donation Management System. It consists of five main tables: Users, Donors, Blood Requests, Donations, and Blood Stock.

## 2. Relations

### 2.1 Users

**Users** (`user_id`, name, email, password, phone, role)

- Primary Key: `user_id`
- Unique Key: `email`

### 2.2 Donors

**Donors** (`donor_id`, user_id, blood_group, age, gender, address, city, availability)

- Primary Key: `donor_id`
- Foreign Key: `user_id` references Users(`user_id`)

### 2.3 Blood Requests

**Blood_Requests** (`request_id`, patient_name, blood_group, units_required, hospital_name, city, contact_number, request_date, status, user_id)

- Primary Key: `request_id`
- Foreign Key: `user_id` references Users(`user_id`)

### 2.4 Donations

**Donations** (`donation_id`, donor_id, donation_date, units_donated, hospital_name, status)

- Primary Key: `donation_id`
- Foreign Key: `donor_id` references Donors(`donor_id`)

### 2.5 Blood Stock

**Blood_Stock** (`stock_id`, blood_group, available_units, last_updated)

- Primary Key: `stock_id`
- Unique Key: `blood_group`

## 3. Relationship Mapping

| Parent Table | Child Table | Relationship |
|---|---|---|
| Users | Donors | One-to-Many |
| Users | Blood_Requests | One-to-Many |
| Donors | Donations | One-to-Many |
| Blood Stock | — | Independent Table |

## 4. Keys Used

- **Primary Key:** Uniquely identifies each record in a table.
- **Foreign Key:** Establishes a relationship between two tables.
- **Unique Key:** Prevents duplicate values in a column, such as user email and blood group in stock.

## 5. Conclusion

The relational model organizes donor information, donation records, blood requests, and blood stock into structured tables. Primary and foreign keys help maintain relationships and data consistency.