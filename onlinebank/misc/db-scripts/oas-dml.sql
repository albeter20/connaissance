-- select customer_id,count(*) from org_node group by customer_id having count(1) < 50;
-- Got customer id:3774

-- Create customer
---------------------------------------------
-- CREATE THESE 2 CUSTOMERS FIRST
insert into CUSTOMER(CUSTOMER_ID, CUSTOMER_NAME, BILLING_ADDRESS_ID, MAILING_ADDRESS_ID, CONTACT_NAME, CONTACT_PHONE, CONTACT_EMAIL, STATE_PR, COUNTRY, EXT_CUSTOMER_ID, CTB_CONTACT_NAME, CTB_CONTACT_EMAIL, ALLOW_DATA_UPLOAD, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, EISS_ORG, TOP_ORG_NAME, SEASON_YEAR, OVERRIDE_HIDE_ACCOMMODATIONS, IMPORT_STUDENT_EDITABLE, DEMOGRAPHIC_VISIBLE, ACTIVE_PROGRAM_ID, ACTIVE_PROGRAM_NAME, ACTIVE_PROGRAM_START_DATE, ACTIVE_PROGRAM_END_DATE)
values (1001, 'Root', NULL,NULL, '', '8313930700', '', 'CA', 'US', '', '', '', 'F', 1, NOW(), null, NULL, 'AC', '', '', '', 'F', 'T', 'T', 1, 'Root Program', '2006-10-10 19:03:34','2018-10-10 19:03:34');

insert into customer (CUSTOMER_ID, CUSTOMER_NAME, BILLING_ADDRESS_ID, MAILING_ADDRESS_ID, CONTACT_NAME, CONTACT_PHONE, CONTACT_EMAIL, STATE_PR, COUNTRY, EXT_CUSTOMER_ID, CTB_CONTACT_NAME, CTB_CONTACT_EMAIL, ALLOW_DATA_UPLOAD, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, EISS_ORG, TOP_ORG_NAME, SEASON_YEAR, OVERRIDE_HIDE_ACCOMMODATIONS, IMPORT_STUDENT_EDITABLE, DEMOGRAPHIC_VISIBLE, ACTIVE_PROGRAM_ID, ACTIVE_PROGRAM_NAME, ACTIVE_PROGRAM_START_DATE, ACTIVE_PROGRAM_END_DATE)
values (1002, 'CTB', NULL,NULL, '', '8313930700', '', 'CA', 'US', '', '', '', 'F', 1, NOW(), null, NULL, 'AC', '', '', '', 'F', 'T', 'T', 2, 'CTB Program', '2006-10-10 19:03:34','2018-10-10 19:03:34');


INSERT INTO CUSTOMER(CUSTOMER_NAME,CONTACT_NAME,CONTACT_PHONE,CONTACT_EMAIL,STATE_PR,COUNTRY,CTB_CONTACT_NAME,CTB_CONTACT_EMAIL,CREATED_BY,CREATED_DATE_TIME,ACTIVATION_STATUS,OVERRIDE_HIDE_ACCOMMODATIONS,IMPORT_STUDENT_EDITABLE,DEMOGRAPHIC_VISIBLE,ACTIVE_PROGRAM_ID,ACTIVE_PROGRAM_NAME,ACTIVE_PROGRAM_START_DATE,ACTIVE_PROGRAM_END_DATE)
VALUES('West ABE','Kathy Anderson','(763)682-8567x','a@ctb.com','MN','US','Nadia Greer','b@ctb.com',9,NOW(),'AC','F','T','T',3774,'West ABE','2008-05-31 5:30:44 ','2016-05-31 5:30:44');
-- FIRSTCASE:CUSTOMER
insert into CUSTOMER (CUSTOMER_NAME, BILLING_ADDRESS_ID, MAILING_ADDRESS_ID, CONTACT_NAME, CONTACT_PHONE, CONTACT_EMAIL, STATE_PR, COUNTRY, EXT_CUSTOMER_ID, CTB_CONTACT_NAME, CTB_CONTACT_EMAIL, ALLOW_DATA_UPLOAD, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, EISS_ORG, TOP_ORG_NAME, SEASON_YEAR, OVERRIDE_HIDE_ACCOMMODATIONS, IMPORT_STUDENT_EDITABLE, DEMOGRAPHIC_VISIBLE, ACTIVE_PROGRAM_ID, ACTIVE_PROGRAM_NAME, ACTIVE_PROGRAM_START_DATE, ACTIVE_PROGRAM_END_DATE)
values ('Laslink License Dev 2015', null, null, 'Somenath', '(831)111-1111x1111', 'somenath.c@tcs.comv', 'AZ', '', '', 'Somenath', 'somenath.c@tcs.comv', 'F', 160040,NOW(), null, null, 'AC', '', '', '', 'F', 'F', 'T', 17013, 'Laslink License Dev 2015', '2008-05-31 5:30:44 ','2016-05-31 5:30:44');



-- ORG_NODE_CATEGORY
--------------------------------------
-- CREATE THESE NODES FIRST
--------------------------------------
insert into ORG_NODE_CATEGORY (ORG_NODE_CATEGORY_ID, CUSTOMER_ID, CATEGORY_LEVEL, CATEGORY_NAME, IS_GROUP, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS)
values (1001, 1001, 1, 'Root', 'F', 1, NOW(), null, NULL, 'AC');

insert into ORG_NODE_CATEGORY (ORG_NODE_CATEGORY_ID, CUSTOMER_ID, CATEGORY_LEVEL, CATEGORY_NAME, IS_GROUP, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS)
values (1002, 1002, 1, 'CTB', 'F', 1,NOW(), null, NULL, 'AC');

insert into ORG_NODE_CATEGORY (CUSTOMER_ID, CATEGORY_LEVEL, CATEGORY_NAME, IS_GROUP, CREATED_BY, CREATED_DATE_TIME,ACTIVATION_STATUS)
values ( 1, 1, 'Catapult Learning', 'F', 154430, NOW(),'AC');

insert into ORG_NODE_CATEGORY (CUSTOMER_ID, CATEGORY_LEVEL, CATEGORY_NAME, IS_GROUP, CREATED_BY, CREATED_DATE_TIME,ACTIVATION_STATUS)
values (1, 2, 'Contract', 'F', 1, NOW(),'AC');

insert into ORG_NODE_CATEGORY (CUSTOMER_ID, CATEGORY_LEVEL, CATEGORY_NAME, IS_GROUP, CREATED_BY, CREATED_DATE_TIME,ACTIVATION_STATUS)
values (1, 3, 'Customer Site', 'F', 1, NOW(),'AC');

insert into ORG_NODE_CATEGORY (CUSTOMER_ID, CATEGORY_LEVEL, CATEGORY_NAME, IS_GROUP, CREATED_BY, CREATED_DATE_TIME,ACTIVATION_STATUS)
values (1, 4, 'Program Year', 'F', 1, NOW(),'AC');

-- FIRSTCASE:ORG_NODE_CATEGORY
insert into ORG_NODE_CATEGORY (CUSTOMER_ID, CATEGORY_LEVEL, CATEGORY_NAME, IS_GROUP, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS)
values (2,1, 'State', 'F', 160040,NOW(),NULL,NULL, 'AC');

insert into ORG_NODE_CATEGORY (CUSTOMER_ID, CATEGORY_LEVEL, CATEGORY_NAME, IS_GROUP, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS)
values (2, 2, 'District', 'F', 160040,NOW(),NULL,NULL, 'AC');

insert into ORG_NODE_CATEGORY (CUSTOMER_ID, CATEGORY_LEVEL, CATEGORY_NAME, IS_GROUP, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS)
values (2, 3, 'School', 'F', 160040,NOW(), NULL,NULL, 'AC');

insert into ORG_NODE_CATEGORY (CUSTOMER_ID, CATEGORY_LEVEL, CATEGORY_NAME, IS_GROUP, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS)
values (2, 4, 'Class', 'F', 160040, NOW(), NULL,NULL, 'AC');


-- ORGN_NODE
-------------------------------------------
-- CREATE THESE 2 NODES FIRST
-------------------------------------------
insert into org_node (ORG_NODE_ID, CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (1001, 1001, 1001, 'Root', '', '', '', 'Root Node', 1, NOW(), null, NULL, 'AC', null, '', '', '', '', '', '', '');

insert into org_node (ORG_NODE_ID, CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (1002, 1002,1002, 'CTB', '', '', '', 'CTB Node', 1, NOW(), null, NULL, 'AC', null, '', '', '', '', 'CTB', '', '');

---- THEN THESE

insert into ORG_NODE (CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (2, 5, 'Laslink License Dev 2015', '', '', '', '', 160040, NOW(), null, NOW(), 'AC', null, '', '', '', '', '', '', '21212541');

insert into ORG_NODE (CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (2, 7, 'School1', '', '', '', '', 228142, NOW(), null, NOW(), 'AC', null, '', '', '', '', 'S001', '', '20202013');

insert into ORG_NODE (CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (2, 8, 'Class1', '', '', '', '', 228142, NOW(), null, NOW(), 'AC', null, '', '', '', '', 'C001', '', '20202015');

insert into ORG_NODE (CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (2, 6, 'District1', '', '', '', '', 228142,NOW(), null, NOW(), 'AC', null, '', '', '', '', 'D001', '', '20202011');

insert into ORG_NODE ( CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (2, 7, 'School2', '', '', '', '', 228142,NOW(), null, NOW(), 'AC', null, '', '', '', '', 'S002', '', '20202014');

insert into ORG_NODE (CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (2, 8, 'Class3', '', '', '', '', 228142,NOW(), null, NOW(), 'AC', null, '', '', '', '', 'C003', '', '20202017');

insert into ORG_NODE (CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (2, 8, 'Class9', '', '', '', '', 228142,NOW(), null, NOW(), 'AC', null, '', '', '', '', '', '', '23456543');

insert into ORG_NODE (CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (2, 6, 'District2', '', '', '', '', 228142,NOW(), null,NOW(), 'AC', null, '', '', '', '', 'D002', '', '20202012');

insert into ORG_NODE (CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (2, 8, 'Class2', '', '', '', '', 228142,NOW(), null, NOW(), 'AC', null, '', '', '', '', 'C002', '', '20202016');

insert into ORG_NODE (CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (2, 8, 'Class4', '', '', '', '', 228142,NOW(), null,NOW(), 'AC', null, '', '', '', '', 'C004', '', '20202019');

insert into ORG_NODE (CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (2, 7, 'School9', '', '', '', '', 228142,NOW(), null,NOW(), 'AC', null, '', '', '', '', '', '', '76754321');

insert into ORG_NODE (CUSTOMER_ID, ORG_NODE_CATEGORY_ID, ORG_NODE_NAME, EXT_QED_PIN, EXL_ELM_ID, EXT_ORG_NODE_TYPE, ORG_NODE_DESCRIPTION, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, ACTIVATION_STATUS, DATA_IMPORT_HISTORY_ID, PARENT_STATE, PARENT_REGION, PARENT_COUNTRY, PARENT_DISTRICT, ORG_NODE_CODE, INFA_LEVEL_CODE, ORG_NODE_MDR_NUMBER)
values (2, 6, 'Distric3', '', '', '', '', 228142,NOW(), null,NOW(), 'AC', null, '', '', '', '', '', '', '12233567');
------------------------------------------------------------------
-- ORG_NODE_ANCESTOR
------------------------------------------------------------------
insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1001, 1, 2, 160040, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1002, 1, 1, 160040, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1, 1, 0, 160040, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1001, 2, 4, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1002, 2, 3, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1, 2, 2, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (4, 2, 1, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (2, 2, 0, 228142,NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1001, 3, 5, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1002, 3, 4, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1, 3, 3, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (4, 3, 2, 228142,NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (2, 3, 1, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (3, 3, 0, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (4, 4, 0, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1001, 4, 3, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1002, 4, 2, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1, 4, 1, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1001, 5, 4, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1002, 5, 3, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1, 5, 2, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (4, 5, 1, 228142,NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (5, 5, 0, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1001, 6, 5, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1002, 6, 4, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1, 6, 3, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (4, 6, 2, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (5, 6, 1, 228142,NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (6, 6, 0, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (8, 7, 2, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1001, 7, 5, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1002, 7, 4, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1, 7, 3, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (11, 7, 1, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (7, 7, 0, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (12, 12, 0, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1001, 12, 3, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1002, 12, 2, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1, 12, 1, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (8, 8, 0, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1001, 8, 3, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1002, 8, 2, 228142,NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1, 8, 1, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1001, 9, 5, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1002, 9, 4, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1, 9, 3, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (4, 9, 2, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (2, 9, 1, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (9, 9, 0, 228142,NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (4, 10, 2, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1001, 10, 5, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1002, 10, 4, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1, 10, 3, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (5, 10, 1, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (10, 10, 0, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (8, 11, 1, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1001, 11, 4, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1002, 11, 3, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (1, 11, 2, 228142, NOW(), null, null, null);

insert into ORG_NODE_ANCESTOR (ANCESTOR_ORG_NODE_ID, ORG_NODE_ID, NUMBER_OF_LEVELS, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, DATA_IMPORT_HISTORY_ID, UPDATED_DATE_TIME)
values (11, 11, 0, 228142, NOW(), null, null, null);

-----------------------------------------------------------------------------------------------------------------
--ORG_NODE_PARENT
------------------------------------------------------------------------------------------------------------------
insert into org_node_parent (PARENT_ORG_NODE_ID, ORG_NODE_ID, CUSTOMER_ID, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, DATA_IMPORT_HISTORY_ID)
values (11, 7, 2, 228142, NOW(), null, NULL, null);

insert into org_node_parent (PARENT_ORG_NODE_ID, ORG_NODE_ID, CUSTOMER_ID, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, DATA_IMPORT_HISTORY_ID)
values (2, 3, 2, 228142, NOW(), null, NULL, null);

insert into org_node_parent (PARENT_ORG_NODE_ID, ORG_NODE_ID, CUSTOMER_ID, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, DATA_IMPORT_HISTORY_ID)
values (4, 2, 2, 228142, NOW(), null, NULL, null);

insert into org_node_parent (PARENT_ORG_NODE_ID, ORG_NODE_ID, CUSTOMER_ID, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, DATA_IMPORT_HISTORY_ID)
values (2, 1, 2, 160040, NOW(), null, NULL, null);

insert into org_node_parent (PARENT_ORG_NODE_ID, ORG_NODE_ID, CUSTOMER_ID, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, DATA_IMPORT_HISTORY_ID)
values (4, 5, 2, 228142, NOW(), null, NULL, null);

insert into org_node_parent (PARENT_ORG_NODE_ID, ORG_NODE_ID, CUSTOMER_ID, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, DATA_IMPORT_HISTORY_ID)
values (5, 6, 2, 228142, NOW(), null,NULL, null);

insert into org_node_parent (PARENT_ORG_NODE_ID, ORG_NODE_ID, CUSTOMER_ID, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, DATA_IMPORT_HISTORY_ID)
values (1, 4, 2, 228142, NOW(), null, NULL, null);

insert into org_node_parent (PARENT_ORG_NODE_ID, ORG_NODE_ID, CUSTOMER_ID, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, DATA_IMPORT_HISTORY_ID)
values (1, 8, 2, 228142, NOW(), null, NULL, null);

insert into org_node_parent (PARENT_ORG_NODE_ID, ORG_NODE_ID, CUSTOMER_ID, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, DATA_IMPORT_HISTORY_ID)
values (2, 9, 2, 228142, NOW(), null, NULL, null);

insert into org_node_parent (PARENT_ORG_NODE_ID, ORG_NODE_ID, CUSTOMER_ID, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, DATA_IMPORT_HISTORY_ID)
values (5, 10, 2, 228142, NOW(), null, NULL, null);

insert into org_node_parent (PARENT_ORG_NODE_ID, ORG_NODE_ID, CUSTOMER_ID, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, DATA_IMPORT_HISTORY_ID)
values (8, 11, 2, 228142, NOW(), null, NULL, null);

insert into org_node_parent (PARENT_ORG_NODE_ID, ORG_NODE_ID, CUSTOMER_ID, CREATED_BY, CREATED_DATE_TIME, UPDATED_BY, UPDATED_DATE_TIME, DATA_IMPORT_HISTORY_ID)
values (1, 12, 2, 228142, NOW(), null, NULL, null);







