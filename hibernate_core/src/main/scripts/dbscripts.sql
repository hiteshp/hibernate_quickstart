-- mysql
-- create new database
CREATE database customerdb;


-- switch to database
USE customerdb;


-- create customer table

CREATE TABLE customer (
customerId INT PRIMARY KEY AUTO_INCREMENT,
customerName VARCHAR(50),
customerEmail VARCHAR(60),
customerPhone INT(15));