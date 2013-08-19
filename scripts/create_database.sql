drop table MESSAGE, AGENT, REGISTERED_AT, NODE, SERVICE;

CREATE TABLE MESSAGE (
	ID INT NOT NULL auto_increment,
	EVENT VARCHAR(255) NOT NULL,
	TIME_STAMP BIGINT NOT NULL,
	SOURCE_NODE VARCHAR(255),
	SOURCE_AGENT BIGINT,
	DESTINATION_NODE VARCHAR(255),
	DESTINATION_AGENT BIGINT,
	REMARKS VARCHAR(255),
	CONSTRAINT MESSAGE_PK PRIMARY KEY (ID),
	CONSTRAINT SOURCE_NODE_FK FOREIGN KEY (SOURCE_NODE) REFERENCES NODE(NODE_ID),
	CONSTRAINT DESTINATION_NODE_FK FOREIGN KEY (DESTINATION_NODE) REFERENCES NODE(NODE_ID),
	CONSTRAINT SOURCE_AGENT_FK FOREIGN KEY (SOURCE_AGENT) REFERENCES AGENT(AGENT_ID),
	CONSTRAINT DESTINATION_AGENT_FK FOREIGN KEY (DESTINATION_AGENT) REFERENCES AGENT(AGENT_ID)
);


CREATE TABLE AGENT (
	AGENT_ID BIGINT NOT NULL,
	TYPE ENUM("USER", "SERVICE", "GROUP", "MONITORING", "MEDIATOR"),
	CONSTRAINT AGENT_PK PRIMARY KEY (AGENT_ID)
);


CREATE TABLE REGISTERED_AT (
	REGISTRATION_DATE TIMESTAMP NULL,
	UNREGISTRATION_DATE TIMESTAMP NULL,
	AGENT_ID BIGINT NOT NULL,
	RUNNING_AT VARCHAR(255),
	CONSTRAINT AGENT_ID_FK FOREIGN KEY (AGENT_ID) REFERENCES AGENT(AGENT_ID),
	CONSTRAINT RUNNING_AT_FK FOREIGN KEY (RUNNING_AT) REFERENCES NODE(NODE_ID)
);


CREATE TABLE NODE (
	NODE_ID VARCHAR(255) NOT NULL,
	NODE_LOCATION VARCHAR(255) NOT NULL,
	CONSTRAINT NODE_PK PRIMARY KEY (NODE_ID)
);


CREATE TABLE SERVICE (
	AGENT_ID BIGINT NOT NULL,
	SERVICE_CLASS_NAME VARCHAR(255),
	CONSTRAINT SERVICE_PK PRIMARY KEY (AGENT_ID),
	CONSTRAINT SERVICE_FK FOREIGN KEY (AGENT_ID) REFERENCES AGENT(AGENT_ID),
	CONSTRAINT SERVICE_UK UNIQUE KEY (AGENT_ID,SERVICE_CLASS_NAME)
);
