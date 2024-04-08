DROP TABLE IF EXISTS PUBLIC.USERS

CREATE TABLE IF NOT EXISTS USERS (
	USER_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	USER_NAME VARCHAR(255) NOT NULL,
	USER_EMAIL VARCHAR(255) UNIQUE NOT NULL,
	CONSTRAINT USERS_PK PRIMARY KEY (USER_ID)
);