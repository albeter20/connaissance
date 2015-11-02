-- DDL scripts for OAS schema
CREATE DATABASE OAS;
USE OAS;
-- FIRST SET OF TABLE CREATION

-- ADDRESS TABLE
CREATE TABLE ADDRESS(
	ADDRESS_ID INTEGER NOT NULL AUTO_INCREMENT,
	STREET_LINE1 VARCHAR(64),
	STREET_LINE2 VARCHAR(64),
	STREET_LINE3 VARCHAR(64),
	CITY VARCHAR(64),
	STATEPR VARCHAR(2),
	COUNTRY VARCHAR(3),
	ZIPCODE VARCHAR(15),
	PRIMARY_PHONE VARCHAR(32),
	FAX VARCHAR(32),
	DATA_IMPORT_HISTORY_ID INTEGER,
	SECONDARY_PHONE VARCHAR(32),
	SECONDARY_PHONE_EXT VARCHAR(32),
	PRIMARY_PHONE_EXT VARCHAR(32),
	CREATED_BY INTEGER,
	CREATED_DATE_TIME DATETIME,
	UPDATED_BY INTEGER,
	UPDATED_DATE_TIME DATETIME,
	PRIMARY KEY(ADDRESS_ID)
);

-- CUSTOMER TABLE

CREATE TABLE CUSTOMER(
	CUSTOMER_ID INTEGER NOT NULL AUTO_INCREMENT,
	CUSTOMER_NAME VARCHAR(64),
	CONTACT_NAME VARCHAR(64),
	CONTACT_PHONE VARCHAR(32),
	CONTACT_EMAIL VARCHAR(64),
	COUNTRY VARCHAR(3),
	STATE_PR VARCHAR(2),
	CREATED_DATE_TIME DATETIME,
	EXT_CUSTOMER_ID VARCHAR(32),
	BILLING_ADDRESS_ID INTEGER,
	CREATED_BY INTEGER,
	UPDATED_BY INTEGER,
	UPDATED_DATE_TIME DATETIME,
	CTB_CONTACT_NAME VARCHAR(64),
	ACTIVATION_STATUS VARCHAR(2),
	MAILING_ADDRESS_ID INTEGER,
	CTB_CONTACT_EMAIL VARCHAR(64),
	ALLOW_DATA_UPLOAD VARCHAR(2),
	EISS_ORG VARCHAR(8),
	TOP_ORG_NAME VARCHAR(8),
	SEASON_YEAR VARCHAR(6),
	OVERRIDE_HIDE_ACCOMMODATIONS VARCHAR(16),
	IMPORT_STUDENT_EDITABLE CHAR(1),
	DEMOGRAPHIC_VISIBLE CHAR(1),
	ACTIVE_PROGRAM_ID NUMERIC,
	ACTIVE_PROGRAM_START_DATE DATETIME,
	ACTIVE_PROGRAM_END_DATE DATETIME,
	ACTIVE_PROGRAM_NAME VARCHAR(64),
	PRIMARY KEY(CUSTOMER_ID),
	FOREIGN KEY(BILLING_ADDRESS_ID) 
	REFERENCES ADDRESS(ADDRESS_ID)
	ON DELETE CASCADE
	ON UPDATE CASCADE,
	FOREIGN KEY(MAILING_ADDRESS_ID) 
	REFERENCES ADDRESS(ADDRESS_ID)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

-- ORG NODE CATEGORY

CREATE TABLE ORG_NODE_CATEGORY(
	ORG_NODE_CATEGORY_ID INTEGER NOT NULL AUTO_INCREMENT,
	CATEGORY_NAME VARCHAR(64),
	CUSTOMER_ID INTEGER,
	CATEGORY_LEVEL NUMERIC(4),
	IS_GROUP VARCHAR(2),
	CREATED_BY INTEGER,
	CREATED_DATE_TIME DATETIME,
	UPDATED_BY INTEGER,
	UPDATED_DATE_TIME DATETIME,
	ACTIVATION_STATUS VARCHAR(2),
	PRIMARY KEY(ORG_NODE_CATEGORY_ID),
	FOREIGN KEY(CUSTOMER_ID) 
	REFERENCES CUSTOMER(CUSTOMER_ID)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

-- ORG_NODE

CREATE TABLE ORG_NODE(
	ORG_NODE_ID INTEGER NOT NULL AUTO_INCREMENT,
	ORG_NODE_CATEGORY_ID INTEGER,
	ORG_NODE_NAME VARCHAR(64),
	EXT_QED_PIN VARCHAR(32),
	EXL_ELM_ID VARCHAR(32),
	CUSTOMER_ID INTEGER,
	CREATED_BY INTEGER,
	CREATED_DATE_TIME DATETIME,
	ORG_NODE_DESCRIPTION VARCHAR(255),
	ORG_NODE_MDR_NUMBER VARCHAR(32),
	UPDATED_BY INTEGER,
	UPDATED_DATE_TIME DATETIME,
	ACTIVATION_STATUS VARCHAR(2),
	DATA_IMPORT_HISTORY_ID INTEGER,
	EXT_ORG_NODE_TYPE VARCHAR(32),
	PARENT_STATE VARCHAR(32),
	PARENT_REGION VARCHAR(32),
	PARENT_COUNTRY VARCHAR(2),
	PARENT_DISTRICT VARCHAR(32),
	ORG_NODE_CODE VARCHAR(32),
	INFA_LEVEL_CODE VARCHAR(255),
	PRIMARY KEY(ORG_NODE_ID),
	FOREIGN KEY(ORG_NODE_CATEGORY_ID) 
	REFERENCES ORG_NODE_CATEGORY(ORG_NODE_CATEGORY_ID)
	ON DELETE CASCADE
	ON UPDATE CASCADE,
	FOREIGN KEY(CUSTOMER_ID) 
	REFERENCES CUSTOMER(CUSTOMER_ID)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

--------------------------------------------------------------------------------------------------
-- User related tables
--------------------------------------------------------------------------------------------------

CREATE TABLE ROLE_TYPE(
	ROLE_TYPE_ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME VARCHAR(255),
	DESCRIPTION VARCHAR(255),
	APPLICATION VARCHAR(4),
	PRIMARY KEY(ROLE_TYPE_ID)
);

-- ROLE MASTER TABLE

CREATE TABLE ROLE(
	ROLE_ID INTEGER NOT NULL AUTO_INCREMENT,
	ROLE_NAME VARCHAR(64),
	ACTIVATION_STATUS VARCHAR(2),
	ROLE_TYPE_ID INTEGER NOT NULL,
	CREATED_BY INTEGER,
	CREATED_DATE_TIME DATETIME,
	UPDATED_BY INTEGER,
	UPDATED_DATE_TIME DATETIME,
	PRIMARY KEY(ROLE_ID),
	FOREIGN KEY(ROLE_TYPE_ID) 
	REFERENCES ROLE_TYPE(ROLE_TYPE_ID) 
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE PASSWORD_HINT_QUESTION(
	PASSWORD_HINT_QUESTION_ID INTEGER NOT NULL AUTO_INCREMENT,
	PASSWORD_HINT_QUESTION VARCHAR(255),
	ACTIVATION_STATUS VARCHAR(2),
	CREATED_BY INTEGER,
	CREATED_DATE_TIME DATETIME,
	UPDATED_BY INTEGER,
	UPDATED_DATE_TIME DATETIME,
	PRIMARY KEY(PASSWORD_HINT_QUESTION_ID)
);

-- USERS TABLE

CREATE TABLE USERS(
	USER_ID INTEGER NOT NULL AUTO_INCREMENT,
	USER_NAME VARCHAR(32),
	PASSWORD VARCHAR(32),
	FIRST_NAME VARCHAR(32),
	MIDDLE_NAME VARCHAR(32),
	LAST_NAME VARCHAR(32),
	EMAIL VARCHAR(64),
	PASSWORD_EXPIRATION_DATE DATETIME,
	ADDRESS_ID INTEGER,
	ACTIVE_SESSION VARCHAR(255),
	TIMEZONE VARCHAR(255),
	PASSWORD_HINT_ANSWER VARCHAR(255),
	PASSWORD_HINT_QUESTION_ID INTEGER,
	RESET_PASSWORD VARCHAR(2),
	LAST_LOGIN_DATE_TIME DATETIME,
	PREFIX VARCHAR(8),
	SUFFIX VARCHAR(8),
	PREFERRED_NAME VARCHAR(32),
	EXT_SCHOOL_ID VARCHAR(32),
	DATA_IMPORT_HISTORY_ID INTEGER,
	EXT_PIN1 VARCHAR(32),
	EXT_PIN2 VARCHAR(32),
	EXT_PIN3 VARCHAR(32),
	ACTIVATION_STATUS VARCHAR(2),
	DISPLAY_USER_NAME VARCHAR(32),
	DISPLAY_NEW_MESSAGE VARCHAR(2),
	CREATED_BY INTEGER,
	CREATED_DATE_TIME DATETIME,
	UPDATED_BY INTEGER,
	UPDATED_DATE_TIME DATETIME,
	PRIMARY KEY(USER_ID),
	FOREIGN KEY (ADDRESS_ID) 
	REFERENCES ADDRESS(ADDRESS_ID) 
	ON DELETE CASCADE
	ON UPDATE CASCADE,
	FOREIGN KEY (PASSWORD_HINT_QUESTION_ID)
    REFERENCES 	PASSWORD_HINT_QUESTION(PASSWORD_HINT_QUESTION_ID)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE USER_ROLE(
	USER_ID INTEGER NOT NULL,
	ROLE_ID INTEGER NOT NULL,
	ORG_NODE_ID INTEGER NOT NULL,
	ACTIVATION_STATUS VARCHAR(2),
	DATA_IMPORT_HISTORY_ID INTEGER,
	CREATED_BY INTEGER,
	CREATED_DATE_TIME DATETIME,
	UPDATED_BY INTEGER,
	UPDATED_DATE_TIME DATETIME,
	PRIMARY KEY(USER_ID,ROLE_ID,ORG_NODE_ID),
	FOREIGN KEY(USER_ID) REFERENCES USERS(USER_ID)
	ON DELETE CASCADE
	ON UPDATE CASCADE,
	FOREIGN KEY(ROLE_ID) REFERENCES ROLE(ROLE_ID)
	ON DELETE CASCADE
	ON UPDATE CASCADE,
	FOREIGN KEY(ORG_NODE_ID) REFERENCES ORG_NODE(ORG_NODE_ID)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);