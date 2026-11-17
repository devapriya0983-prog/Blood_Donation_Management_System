
-- LifeSaver Blood Donation Management System
-- Database: main
-- MySQL 8.4

CREATE TABLE IF NOT EXISTS users (
    user_id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15) DEFAULT NULL,
    role VARCHAR(20) DEFAULT 'donor',
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS donors (
    donor_id INT NOT NULL AUTO_INCREMENT,
    user_id INT DEFAULT NULL,
    blood_group VARCHAR(5) NOT NULL,
    age INT DEFAULT NULL,
    gender VARCHAR(10) DEFAULT NULL,
    address VARCHAR(255) DEFAULT NULL,
    city VARCHAR(100) DEFAULT NULL,
    availability TINYINT(1) DEFAULT 1,
    PRIMARY KEY (donor_id),
    CONSTRAINT fk_donors_users
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS blood_requests (
    request_id INT NOT NULL AUTO_INCREMENT,
    patient_name VARCHAR(100) NOT NULL,
    blood_group VARCHAR(5) NOT NULL,
    units_required INT NOT NULL,
    hospital_name VARCHAR(150) DEFAULT NULL,
    city VARCHAR(100) DEFAULT NULL,
    contact_number VARCHAR(15) DEFAULT NULL,
    request_date DATE DEFAULT NULL,
    status VARCHAR(20) DEFAULT 'Pending',
    user_id INT DEFAULT NULL,
    PRIMARY KEY (request_id),
    CONSTRAINT fk_requests_users
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS blood_stock (
    stock_id INT NOT NULL AUTO_INCREMENT,
    blood_group VARCHAR(5) NOT NULL UNIQUE,
    available_units INT DEFAULT 0,
    last_updated TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (stock_id)
);

CREATE TABLE IF NOT EXISTS donations (
    donation_id INT NOT NULL AUTO_INCREMENT,
    donor_id INT DEFAULT NULL,
    donation_date DATE DEFAULT NULL,
    units_donated INT DEFAULT 1,
    hospital_name VARCHAR(150) DEFAULT NULL,
    status VARCHAR(20) DEFAULT 'Completed',
    PRIMARY KEY (donation_id),
    CONSTRAINT fk_donations_donors
        FOREIGN KEY (donor_id) REFERENCES donors(donor_id)
);

-- Insert the eight standard blood groups if they are missing.
INSERT IGNORE INTO blood_stock (blood_group, available_units)
VALUES
('A+', 0),
('A-', 0),
('B+', 0),
('B-', 0),
('O+', 0),
('O-', 0),
('AB+', 0),
('AB-', 0);

-- Verify table creation.
SHOW TABLES;

-- View blood stock.
SELECT * FROM blood_stock ORDER BY blood_group;
