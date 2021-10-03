DROP TABLE IF EXISTS customer;
CREATE TABLE customer (id SERIAL PRIMARY KEY, first_name VARCHAR(255), last_name VARCHAR(255));

INSERT INTO customer (id, first_name, last_name) values (0, 'Patrik', 'Aradi');
INSERT INTO customer (id, first_name, last_name) values (1, 'Emil', 'Fekete');
INSERT INTO customer (id, first_name, last_name) values (2, 'Dani', 'Sipos');
