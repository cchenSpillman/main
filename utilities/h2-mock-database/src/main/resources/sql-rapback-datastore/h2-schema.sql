/*
 * Unless explicitly acquired and licensed from Licensor under another license, the contents of
 * this file are subject to the Reciprocal Public License ("RPL") Version 1.5, or subsequent
 * versions as allowed by the RPL, and You may not copy or use this file in either source code
 * or executable form, except in compliance with the terms and conditions of the RPL
 *
 * All software distributed under the RPL is provided strictly on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND LICENSOR HEREBY DISCLAIMS ALL SUCH
 * WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, QUIET ENJOYMENT, OR NON-INFRINGEMENT. See the RPL for specific language
 * governing rights and limitations under the RPL.
 *
 * http://opensource.org/licenses/RPL-1.5
 *
 * Copyright 2012-2015 Open Justice Broker Consortium
 */
drop schema if exists rapback_datastore;
CREATE schema rapback_datastore;

--
-- Create tables for subscription notification processor
--
--CREATE TABLE `subscription` (
--  id int(11) NOT NULL AUTO_INCREMENT,
--  topic varchar(100) NOT NULL,
--  startDate date NOT NULL,
--  endDate date DEFAULT NULL,
--  lastValidationDate date DEFAULT NULL,
--  subscribingSystemIdentifier varchar(100) NOT NULL,
--  subscriptionOwner varchar(100) NOT NULL,
--  subjectName varchar(100) NOT NULL,
--  active tinyint(4) NOT NULL,
--  timestamp TIMESTAMP AS CURRENT_TIMESTAMP() 
--);
--
--CREATE TABLE `notification_mechanism` (
--  id int(11) NOT NULL AUTO_INCREMENT,
--  subscriptionId int(11) NOT NULL,
--  notificationMechanismType varchar(20) NOT NULL,
--  notificationAddress varchar(256) NOT NULL,
--  UNIQUE KEY (`subscriptionId`, `notificationAddress`),
--  FOREIGN KEY (subscriptionId)
--  	REFERENCES subscription (id)
--  	ON DELETE CASCADE
--);
--
--CREATE TABLE `subscription_subject_identifier` (
--  id int(11) NOT NULL AUTO_INCREMENT,
--  subscriptionId int(11) NOT NULL,
--  identifierName varchar(100) NOT NULL,
--  identifierValue varchar(256) NOT NULL,
--  
--  FOREIGN KEY (`subscriptionId`)
--  	REFERENCES `subscription` (`id`)
--  	ON DELETE CASCADE
--) ;

CREATE TABLE CIVIL_INITIAL_RESULTS_STATE (STATE VARCHAR(50) NOT NULL);

ALTER TABLE CIVIL_INITIAL_RESULTS_STATE ADD CONSTRAINT STATE PRIMARY KEY (STATE);

CREATE TABLE FBI_RAP_BACK_SUBJECT (SUBJECT_ID INT AUTO_INCREMENT NOT NULL, UCN VARCHAR(100), CRIMINAL_SID VARCHAR(50), CIVIL_SID VARCHAR(50), FIRST_NAME VARCHAR(100) NOT NULL, LAST_NAME VARCHAR(100) NOT NULL, MIDDLE_INITIAL VARCHAR(30), SEX_CODE VARCHAR(1), DOB date);

ALTER TABLE FBI_RAP_BACK_SUBJECT ADD CONSTRAINT FBI_RAP_BACK_SUBJECT_pk PRIMARY KEY (SUBJECT_ID);

CREATE SEQUENCE FBI_RAP_BACK_SUBJECT_SUBJECT_ID_seq_2;

CREATE TABLE IDENTIFICATION_TRANSACTION (TRANSACTION_NUMBER VARCHAR(100) NOT NULL, OTN VARCHAR(100) NOT NULL, TIMESTAMP_RECEIVED TIMESTAMP DEFAULT NOW() NOT NULL, OWNER_ORI VARCHAR(20) NOT NULL, OWNER_PROGRAM_OCA VARCHAR(50) NOT NULL, SUBJECT_ID INT);

ALTER TABLE IDENTIFICATION_TRANSACTION ADD CONSTRAINT LOTC_TRANSACTION_pk PRIMARY KEY (TRANSACTION_NUMBER);

CREATE TABLE CIVIL_FINGER_PRINTS (FINGER_PRINTS_ID INT AUTO_INCREMENT NOT NULL, TRANSACTION_NUMBER VARCHAR(100) NOT NULL, FINGER_PRINTS_FILE BLOB NOT NULL, TRANSACTION_TYPE VARCHAR(100) NOT NULL, FINGER_PRINTS_TYPE VARCHAR(30) NOT NULL, TIMESTAMP TIMESTAMP DEFAULT NOW() NOT NULL);

ALTER TABLE CIVIL_FINGER_PRINTS ADD CONSTRAINT CIVIL_NISTS_pk PRIMARY KEY (FINGER_PRINTS_ID);

CREATE SEQUENCE CIVIL_FINGER_PRINTS_FINGER_PRINTS_ID_seq;

CREATE TABLE CIVIL_INITIAL_RESULTS (CIVIL_INITIAL_RESULT_ID INT AUTO_INCREMENT NOT NULL, TRANSACTION_NUMBER VARCHAR(100) NOT NULL, MATCH_NO_MATCH BOOLEAN NOT NULL, TRANSACTION_TYPE VARCHAR(100) NOT NULL, CIVIL_RAP_BACK_CATEGORY VARCHAR(50) NOT NULL, RESULTS_SENDER VARCHAR(30) NOT NULL, TIMESTAMP TIMESTAMP DEFAULT NOW() NOT NULL, CURRENT_STATE VARCHAR(50) NOT NULL);

ALTER TABLE CIVIL_INITIAL_RESULTS ADD CONSTRAINT CIVIL_INITIAL_RESULTS_pk PRIMARY KEY (CIVIL_INITIAL_RESULT_ID);

CREATE SEQUENCE CIVIL_INITIAL_RESULTS_CIVIL_INITIAL_RESULT_ID_seq;

CREATE TABLE CIVIL_INITIAL_RAP_SHEET (CIVIL_RAPSHEET_ID INT AUTO_INCREMENT NOT NULL, CIVIL_INITIAL_RESULT_ID INT NOT NULL, RAP_SHEET BLOB NOT NULL, TRANSACTION_TYPE VARCHAR(100) NOT NULL, TIMESTAMP TIMESTAMP DEFAULT NOW() NOT NULL);

CREATE SEQUENCE CIVIL_INITIAL_RAP_SHEET_CIVIL_RAPSHEET_ID_seq;

CREATE TABLE CRIMINAL_INITIAL_RESULTS (CRIMINAL_INITIAL_RESULT_ID INT AUTO_INCREMENT NOT NULL, TRANSACTION_NUMBER VARCHAR(100) NOT NULL, MATCH_NO_MATCH BOOLEAN NOT NULL, TRANSACTION_TYPE VARCHAR(100) NOT NULL, RESULTS_SENDER VARCHAR(30) NOT NULL, TIMESTAMP TIMESTAMP DEFAULT NOW() NOT NULL, RAP_BACK_CATEGORY VARCHAR(20) NOT NULL);

ALTER TABLE CRIMINAL_INITIAL_RESULTS ADD CONSTRAINT CRIMINAL_INITIAL_RESULTS_pk PRIMARY KEY (CRIMINAL_INITIAL_RESULT_ID);

CREATE SEQUENCE CRIMINAL_INITIAL_RESULTS_CRIMINAL_INITIAL_RESULT_ID_seq;

CREATE TABLE CRIMINAL_FINGER_PRINTS (FINGER_PRINTS_ID INT AUTO_INCREMENT NOT NULL, TRANSACTION_NUMBER VARCHAR(100) NOT NULL, FINGER_PRINTS_FILE BLOB, TRANSACTION_TYPE VARCHAR(100) NOT NULL, FINGER_PRINTS_TYPE VARCHAR(30) NOT NULL, TIMESTAMP TIMESTAMP DEFAULT NOW() NOT NULL);

ALTER TABLE CRIMINAL_FINGER_PRINTS ADD CONSTRAINT CRIMINAL_NISTS_pk PRIMARY KEY (FINGER_PRINTS_ID);

CREATE SEQUENCE CRIMINAL_FINGER_PRINTS_FINGER_PRINTS_ID_seq;

CREATE TABLE FBI_RAP_BACK_SUBSCRIPTION (FBI_SUBSCRIPTION_ID VARCHAR(100) NOT NULL, SUBJECT_ID INT NOT NULL, RAP_BACK_CATEGORY VARCHAR(100) NOT NULL, SUBSCRIPTION_TERM VARCHAR(50) NOT NULL, RAP_BACK_SUBSCRIPTION_IDENTIFIER VARCHAR(100) NOT NULL, RAP_BACK_EXPIRATION_DATE date NOT NULL, RAP_BACK_START_DATE date NOT NULL, RAP_BACK_OPT_OUT_IN_STATE_INDICATOR BOOLEAN NOT NULL, RAP_BACK_ACTIVITY_NOTIFICATION_FORMAT VARCHAR(100) NOT NULL, TIMESTAMP TIMESTAMP DEFAULT NOW() NOT NULL);

ALTER TABLE FBI_RAP_BACK_SUBSCRIPTION ADD CONSTRAINT FBI_RAP_BACK_SUBSCRIPTION_pk PRIMARY KEY (FBI_SUBSCRIPTION_ID);

CREATE TABLE SUBSEQUENT_RESULTS (SUBSEQUENT_RESULT_ID INT AUTO_INCREMENT NOT NULL, TRANSACTION_NUMBER VARCHAR(100) NOT NULL, FBI_SUBSCRIPTION_ID VARCHAR(100) NOT NULL, RAP_BACK_SUBSCRIPTION_IDENTIFIER VARCHAR(100) NOT NULL, MATCH_NO_MATCH BOOLEAN NOT NULL, RAP_SHEET BLOB NOT NULL, TRANSACTION_TYPE VARCHAR(100) NOT NULL, TIMESTAMP TIMESTAMP DEFAULT NOW() NOT NULL);

CREATE SEQUENCE SUBSEQUENT_RESULTS_SUBSEQUENT_RESULT_ID_seq;

CREATE TABLE SUBSCRIPTION (ID INT AUTO_INCREMENT NOT NULL, TOPIC VARCHAR(100) NOT NULL, STARTDATE date NOT NULL, ENDDATE date DEFAULT null, LASTVALIDATIONDATE date DEFAULT null, SUBSCRIBINGSYSTEMIDENTIFIER VARCHAR(100) NOT NULL, SUBSCRIPTIONOWNER VARCHAR(100) NOT NULL, SUBJECTNAME VARCHAR(100) NOT NULL, ACTIVE TINYINT NOT NULL, TIMESTAMP TIMESTAMP DEFAULT NOW());

ALTER TABLE SUBSCRIPTION ADD CONSTRAINT SUBSCRIPTION_pk PRIMARY KEY (ID);

CREATE UNIQUE INDEX CONSTRAINT_INDEX_9E ON SUBSCRIPTION(ID);

CREATE TABLE CIVIL_FBI_SUBSCRIPTION_RECORD (CIVIL_FBI_SUBSCRIPTION_ID INT AUTO_INCREMENT NOT NULL, SUBSCRIPTION_ID INT NOT NULL, FBI_SUBSCRIPTION_ID VARCHAR(100) NOT NULL, CIVIL_INITIAL_RESULT_ID INT NOT NULL, LAST_MODIFIED_BY VARCHAR(100) NOT NULL, TIMESTAMP VARCHAR(0) DEFAULT 'CURRENT_TIMESTAMP()' NOT NULL);

CREATE SEQUENCE CIVIL_FBI_SUBSCRIPTION_RECORD_CIVIL_FBI_SUBSCRIPTION_ID_seq;

CREATE TABLE CRIMINAL_FBI_SUBSCRIPTION_RECORD (CRIMINAL_FBI_SUBSCRIPTION_ID INT AUTO_INCREMENT NOT NULL, FBI_SUBSCRIPTION_ID VARCHAR(100) NOT NULL, SUBSCRIPTION_ID INT NOT NULL, TIMESTAMP TIMESTAMP DEFAULT NOW() NOT NULL, FBI_OCA VARCHAR(100) NOT NULL);

CREATE SEQUENCE CRIMINAL_FBI_SUBSCRIPTION_RECORD_CRIMINAL_FBI_SUBSCRIPTION_ID_seq;

CREATE TABLE SUBSCRIPTION_SUBJECT_IDENTIFIER (ID INT AUTO_INCREMENT NOT NULL, SUBSCRIPTIONID INT NOT NULL, IDENTIFIERNAME VARCHAR(100) NOT NULL, IDENTIFIERVALUE VARCHAR(256) NOT NULL);

ALTER TABLE SUBSCRIPTION_SUBJECT_IDENTIFIER ADD CONSTRAINT SUBSCRIPTION_SUBJECT_IDENTIFIER_pk PRIMARY KEY (ID);

CREATE INDEX CONSTRAINT_INDEX_B ON SUBSCRIPTION_SUBJECT_IDENTIFIER(SUBSCRIPTIONID);

CREATE TABLE NOTIFICATION_MECHANISM (ID INT AUTO_INCREMENT NOT NULL, SUBSCRIPTIONID INT NOT NULL, NOTIFICATIONMECHANISMTYPE VARCHAR(20) NOT NULL, NOTIFICATIONADDRESS VARCHAR(256) NOT NULL);

ALTER TABLE NOTIFICATION_MECHANISM ADD CONSTRAINT NOTIFICATION_MECHANISM_pk PRIMARY KEY (ID);

CREATE UNIQUE INDEX CONSTRAINT_INDEX_9 ON NOTIFICATION_MECHANISM(SUBSCRIPTIONID, NOTIFICATIONADDRESS);

CREATE INDEX CONSTRAINT_INDEX_94 ON NOTIFICATION_MECHANISM(SUBSCRIPTIONID);

ALTER TABLE CIVIL_INITIAL_RESULTS ADD CONSTRAINT CIVIL_INITIAL_RESULTS_STATE_CIVIL_INITIAL_RESULTS_fk FOREIGN KEY (CURRENT_STATE) REFERENCES CIVIL_INITIAL_RESULTS_STATE (STATE);

ALTER TABLE FBI_RAP_BACK_SUBSCRIPTION ADD CONSTRAINT FBI_RAP_BACK_SUBJECT_FBI_RAP_BACK_SUBSCRIPTION_fk FOREIGN KEY (SUBJECT_ID) REFERENCES FBI_RAP_BACK_SUBJECT (SUBJECT_ID);

ALTER TABLE IDENTIFICATION_TRANSACTION ADD CONSTRAINT FBI_RAP_BACK_SUBJECT_IDENTIFICATION_TRANSACTION_fk FOREIGN KEY (SUBJECT_ID) REFERENCES FBI_RAP_BACK_SUBJECT (SUBJECT_ID) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE CRIMINAL_FINGER_PRINTS ADD CONSTRAINT IDENTIFICATION_TRANSACTION_CRIMINAL_NISTS_fk FOREIGN KEY (TRANSACTION_NUMBER) REFERENCES IDENTIFICATION_TRANSACTION (TRANSACTION_NUMBER) ON DELETE CASCADE;

ALTER TABLE CRIMINAL_INITIAL_RESULTS ADD CONSTRAINT IDENTIFICATION_TRANSACTION_CRIMINAL_INITIAL_RESULTS_fk FOREIGN KEY (TRANSACTION_NUMBER) REFERENCES IDENTIFICATION_TRANSACTION (TRANSACTION_NUMBER);

ALTER TABLE CIVIL_INITIAL_RESULTS ADD CONSTRAINT IDENTIFICATION_TRANSACTION_CIVIL_INITIAL_RESULTS_fk FOREIGN KEY (TRANSACTION_NUMBER) REFERENCES IDENTIFICATION_TRANSACTION (TRANSACTION_NUMBER);

ALTER TABLE CIVIL_FINGER_PRINTS ADD CONSTRAINT IDENTIFICATION_TRANSACTION_CIVIL_NISTS_fk FOREIGN KEY (TRANSACTION_NUMBER) REFERENCES IDENTIFICATION_TRANSACTION (TRANSACTION_NUMBER);

ALTER TABLE SUBSEQUENT_RESULTS ADD CONSTRAINT IDENTIFICATION_TRANSACTION_SUBSEQUENT_RESULTS_fk FOREIGN KEY (TRANSACTION_NUMBER) REFERENCES IDENTIFICATION_TRANSACTION (TRANSACTION_NUMBER);

ALTER TABLE CIVIL_FBI_SUBSCRIPTION_RECORD ADD CONSTRAINT CIVIL_INITIAL_RESULTS_CIVIL_FBI_SUBSCRIPTION_fk FOREIGN KEY (CIVIL_INITIAL_RESULT_ID) REFERENCES CIVIL_INITIAL_RESULTS (CIVIL_INITIAL_RESULT_ID);

ALTER TABLE CIVIL_INITIAL_RAP_SHEET ADD CONSTRAINT CIVIL_INITIAL_RESULTS_CIVIL_INITIAL_RAP_SHEET_fk FOREIGN KEY (CIVIL_INITIAL_RESULT_ID) REFERENCES CIVIL_INITIAL_RESULTS (CIVIL_INITIAL_RESULT_ID);

ALTER TABLE CRIMINAL_FBI_SUBSCRIPTION_RECORD ADD CONSTRAINT FBI_RAP_BACK_SUBSCRIPTION_CRIMINAL_FBI_SUBSCRIPTION_RECORD_fk FOREIGN KEY (FBI_SUBSCRIPTION_ID) REFERENCES FBI_RAP_BACK_SUBSCRIPTION (FBI_SUBSCRIPTION_ID);

ALTER TABLE CIVIL_FBI_SUBSCRIPTION_RECORD ADD CONSTRAINT FBI_RAP_BACK_SUBSCRIPTION_CIVIL_FBI_SUBSCRIPTION_fk FOREIGN KEY (FBI_SUBSCRIPTION_ID) REFERENCES FBI_RAP_BACK_SUBSCRIPTION (FBI_SUBSCRIPTION_ID);

ALTER TABLE SUBSEQUENT_RESULTS ADD CONSTRAINT FBI_RAP_BACK_SUBSCRIPTION_SUBSEQUENT_RESULTS_fk FOREIGN KEY (FBI_SUBSCRIPTION_ID) REFERENCES FBI_RAP_BACK_SUBSCRIPTION (FBI_SUBSCRIPTION_ID);

ALTER TABLE NOTIFICATION_MECHANISM ADD CONSTRAINT CONSTRAINT_94 FOREIGN KEY (SUBSCRIPTIONID) REFERENCES SUBSCRIPTION (ID) ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE SUBSCRIPTION_SUBJECT_IDENTIFIER ADD CONSTRAINT CONSTRAINT_B FOREIGN KEY (SUBSCRIPTIONID) REFERENCES SUBSCRIPTION (ID) ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE CRIMINAL_FBI_SUBSCRIPTION_RECORD ADD CONSTRAINT SUBSCRIPTION_CRIMINAL_FBI_SUBSCRIPTION_RECORD_fk FOREIGN KEY (SUBSCRIPTION_ID) REFERENCES SUBSCRIPTION (ID);

ALTER TABLE CIVIL_FBI_SUBSCRIPTION_RECORD ADD CONSTRAINT SUBSCRIPTION_CIVIL_FBI_SUBSCRIPTION_fk FOREIGN KEY (SUBSCRIPTION_ID) REFERENCES SUBSCRIPTION (ID);
