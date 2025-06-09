-- Drop and recreate the database
DROP DATABASE IF EXISTS crm;
CREATE DATABASE crm;
USE crm;

-- Users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    role ENUM('admin', 'manager', 'sales', 'customer') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    last_logout TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- Login history table
CREATE TABLE login_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    action ENUM('login', 'logout') NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Salesmen table
CREATE TABLE salesmen (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    photo LONGBLOB,
    photo_type VARCHAR(20),
    document LONGBLOB,
    document_type VARCHAR(20),
    document_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Customers table
CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE,
    name VARCHAR(100) NOT NULL,
    company VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    photo LONGBLOB,
    photo_type VARCHAR(20),
    document LONGBLOB,
    document_type VARCHAR(20),
    document_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Orders table
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    salesman_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    order_date DATE NOT NULL,
    status ENUM('pending', 'processing', 'completed', 'cancelled') DEFAULT 'pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (salesman_id) REFERENCES salesmen(id)
);

-- Transactions table
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    transaction_date DATE NOT NULL,
    payment_method ENUM('cash', 'credit_card', 'bank_transfer', 'check', 'other') NOT NULL,
    receipt LONGBLOB,
    receipt_type VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Sample Data

-- 1. Insert an admin user
INSERT INTO users (username, password, full_name, email, phone, role)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqUVs0ZIIf0N3DmFT9kBO/6BniSdK', 'Admin User', 'admin@crm.com', '1234567890', 'admin');

-- 2. Insert a salesman user
INSERT INTO users (username, password, full_name, email, phone, role)
VALUES ('johnsales', 'password123', 'John Salesman', 'john@sales.com', '9876543210', 'sales');

-- 3. Insert the salesman details (user_id = 2)
INSERT INTO salesmen (user_id, name, email, phone)
VALUES (2, 'John Salesman', 'john@sales.com', '9876543210');

-- 4. Insert a customer user
INSERT INTO users (username, password, full_name, email, phone, role)
VALUES ('alice', 'alicepass', 'Alice Customer', 'alice@customer.com', '5551234567', 'customer');

-- 5. Insert the customer details (user_id = 3)
INSERT INTO customers (user_id, name, company, email, phone)
VALUES (3, 'Alice Customer', 'ABC Corp', 'alice@customer.com', '5551234567');

-- 6. Insert sample orders
INSERT INTO orders (customer_id, salesman_id, amount, order_date, status)
VALUES 
(1, 1, 1500.00, '2023-06-01', 'completed'),
(1, 1, 2500.50, '2023-06-15', 'processing');

-- 7. Insert sample transactions
INSERT INTO transactions (order_id, amount, transaction_date, payment_method)
VALUES 
(1, 1500.00, '2023-06-02', 'credit_card'),
(2, 1000.00, '2023-06-16', 'bank_transfer');

-- 8. Create a view to track login history
CREATE VIEW vw_login_history AS
SELECT 
    u.username,
    u.full_name,
    lh.action,
    lh.timestamp,
    lh.ip_address
FROM login_history lh
JOIN users u ON lh.user_id = u.id
ORDER BY lh.timestamp DESC;

select * from users;
select * from salesmen;
