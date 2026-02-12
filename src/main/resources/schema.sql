
-- 1. users
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    role ENUM('resident', 'security', 'admin') NOT NULL,
    is_approved BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. flats
CREATE TABLE flats (
    flat_no VARCHAR(10) PRIMARY KEY,
    block VARCHAR(10),
    floor INT,
    size_sqft INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    FOREIGN KEY (updated_by) REFERENCES users(user_id)
);

-- 3. flat_owners_reference
CREATE TABLE flat_owners_reference (
    ref_id INT PRIMARY KEY AUTO_INCREMENT,
    flat_no VARCHAR(10),
    owner_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    FOREIGN KEY (flat_no) REFERENCES flats(flat_no) ON DELETE CASCADE
);

-- 4. flat_users
CREATE TABLE flat_users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    flat_no VARCHAR(10),
    resident_type ENUM('owner', 'tenant') NOT NULL,
    start_date DATE,
    end_date DATE,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (flat_no) REFERENCES flats(flat_no) ON DELETE CASCADE
);

-- 5. user_documents
CREATE TABLE user_documents (
    doc_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    doc_type ENUM('Aadhar', 'PAN', 'Passport', 'Driving License', 'Other') NOT NULL,
    doc_number VARCHAR(50),
    file_path VARCHAR(255) NOT NULL,
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    verified BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 6. maintenance_bills
CREATE TABLE maintenance_bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    month VARCHAR(10) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status ENUM('Paid', 'Unpaid') DEFAULT 'Unpaid',
    generated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    paid_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 7. payments
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT,
    payment_mode VARCHAR(50),
    transaction_id VARCHAR(100),
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bill_id) REFERENCES maintenance_bills(bill_id) ON DELETE CASCADE
);

-- 8. complaints
CREATE TABLE complaints (
    complaint_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    subject VARCHAR(100),
    description TEXT,
    status ENUM('Pending', 'In Progress', 'Resolved') DEFAULT 'Pending',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 9. notices
CREATE TABLE notices (
    notice_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    description TEXT,
    created_by INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- 10. visitors
CREATE TABLE visitors (
    visitor_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    contact VARCHAR(15),
    flat_no VARCHAR(10),
    in_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    out_time DATETIME,
    logged_by INT,
    FOREIGN KEY (flat_no) REFERENCES flats(flat_no) ON DELETE CASCADE,
    FOREIGN KEY (logged_by) REFERENCES users(user_id) ON DELETE SET NULL
);
