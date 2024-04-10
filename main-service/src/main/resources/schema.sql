DROP TABLE IF EXISTS PUBLIC.USERS;
DROP TABLE IF EXISTS PUBLIC.CATEGORIES;
DROP TABLE IF EXISTS PUBLIC.LOCATIONS;
DROP TABLE IF EXISTS PUBLIC.EVENTS;

CREATE TABLE IF NOT EXISTS USERS (
	USER_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	USER_NAME VARCHAR(255) NOT NULL,
	USER_EMAIL VARCHAR(255) UNIQUE NOT NULL,
	CONSTRAINT USERS_PK PRIMARY KEY (USER_ID)
);

CREATE TABLE IF NOT EXISTS CATEGORIES (
	CATEGORY_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	CATEGORY_NAME VARCHAR(50) UNIQUE NOT NULL,
	CONSTRAINT CATEGORIES_PK PRIMARY KEY (CATEGORY_ID)
);

CREATE TABLE IF NOT EXISTS LOCATIONS (
	LOCATION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	LAT FLOAT NOT NULL,
	LON FLOAT NOT NULL,
	CONSTRAINT LOCATIONS_PK PRIMARY KEY (LOCATION_ID)
);

CREATE TABLE IF NOT EXISTS EVENTS (
    EVENT_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    USER_ID BIGINT NOT NULL,
    ANNOTATION VARCHAR NOT NULL,
    CATEGORY_ID BIGINT NOT NULL,
    DESCRIPTION VARCHAR NOT NULL,
    EVENT_DATE TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    LOCATION_ID BIGINT NOT NULL,
    PAID BOOLEAN NOT NULL,
    PARTICIPANT_LIMIT INTEGER NOT NULL,
    REQUEST_MODERATION BOOLEAN NOT NULL,
    TITLE VARCHAR NOT NULL,
    CREATED_ON TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    PUBLISHED_ON TIMESTAMP WITHOUT TIME ZONE,
    STATE VARCHAR NOT NULL,
    CONSTRAINT EVENTS_PK PRIMARY KEY (EVENT_ID),
    CONSTRAINT EVENTS_USERS_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
    CONSTRAINT EVENTS_CATEGORIES_FK FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORIES(CATEGORY_ID),
    CONSTRAINT EVENTS_LOCATIONS_FK FOREIGN KEY (LOCATION_ID) REFERENCES LOCATIONS(LOCATION_ID)
);

CREATE TABLE IF NOT EXISTS REQUESTS (
    REQUEST_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    EVENT_ID BIGINT NOT NULL
    USER_ID BIGINT NOT NULL,
    STATUS VARCHAR NOT NULL,
    CREATED TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT REQUESTS_PK PRIMARY KEY (REQUEST_ID),
    CONSTRAINT REQUESTS_EVENTS_FK FOREIGN KEY (EVENT_ID) REFERENCES EVENTS(EVENT_ID),
    CONSTRAINT REQUESTS_USERS_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
);