DROP Database CRM;
CREATE DATABASE crm;
USE crm;
CREATE TABLE users (
    id INT AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE salesmen (
    id INT AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    order_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE customers (
    id INT AUTO_INCREMENT,
    transaction_id VARCHAR(255) NOT NULL,
    order_id VARCHAR(255) NOT NULL,
    salesman_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE orders (
    id INT AUTO_INCREMENT,
    salesman_id VARCHAR(255) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT,
    amount VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);
INSERT INTO users (username, password) VALUES ('admin', 'password123');
INSERT INTO users (username, password) VALUES ('salesman1', 'salesman123');

select * from transactions;
select * from salesmen;
select * from orders;
select * from customers;
desc users;
desc salesmen;
desc orders;
desc customers;