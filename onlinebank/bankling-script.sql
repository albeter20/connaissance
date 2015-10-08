-- creating sequence for account table
CREATE SEQUENCE PUBLIC.ACCOUNT_SEQUENCE START WITH 1 INCREMENT BY 1

-- create table account
CREATE TABLE PUBLIC.ACCOUNT
(
ACCOUNT_NO BIGINT PRIMARY KEY NOT NULL,
HOLDER_NAME VARCHAR(25) NOT NULL,
DESCRIPTION VARCHAR(25),
ACCOUNT_TYPE VARCHAR(15) NOT NULL,
STATUS		 VARCHAR(15) NOT NULL, 
OPENING_DATE  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
CLOSING_DATE  TIMESTAMP,
CREATE_DATE  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
CREATED_BY   VARCHAR(15),
MODIFY_DATE  TIMESTAMP,
MODIFIED_BY  VARCHAR(15)
)

-- Database script --------------
create database onlinebank;
use onlinebank;

CREATE TABLE ACCOUNT
(
ACCOUNT_NO BIGINT NOT NULL AUTO_INCREMENT,
HOLDER_NAME VARCHAR(25) NOT NULL,
DESCRIPTION VARCHAR(200),
ACCOUNT_TYPE VARCHAR(15) NOT NULL,
STATUS		 VARCHAR(15) NOT NULL, 
OPENING_DATE  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
CLOSING_DATE  TIMESTAMP,
CREATE_DATE  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
CREATED_BY   VARCHAR(15),
MODIFY_DATE  TIMESTAMP,
MODIFIED_BY  VARCHAR(15),
BALANCE DECIMAL(30,2) DEFAULT 0.00 NOT NULL,
AVAILABLE_BALANCE DECIMAL(30,2) DEFAULT 0.00 NOT NULL,
PRIMARY KEY (ACCOUNT_NO)
);

-- CREATE USER TABLE
CREATE TABLE USER(
USER_ID BIGINT NOT NULL AUTO_INCREMENT,
USERNAME VARCHAR(25) NOT NULL,
CUSTOMER_ID BIGINT NOT NULL,
USER_PUB_TOKEN TEXT NOT NULL,
USER_PVT_TOKEN LONGTEXT NOT NULL,
CREATE_DATE  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
CREATED_BY   VARCHAR(15),
MODIFY_DATE  TIMESTAMP,
MODIFIED_BY  VARCHAR(15),
PRIMARY KEY(USER_ID)
);
INSERT INTO USER(USERNAME,CUSTOMER_ID,USER_PUB_TOKEN,USER_PVT_TOKEN,CREATED_BY) VALUES('INTERNAL',1,,,'SYSTEM');



