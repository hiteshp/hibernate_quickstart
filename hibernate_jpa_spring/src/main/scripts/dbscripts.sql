-- mysql
USE customerdb;

CREATE TABLE Person (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(20) DEFAULT '',
  country VARCHAR(20)
);