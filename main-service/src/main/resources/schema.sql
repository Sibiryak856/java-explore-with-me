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
	LATITUDE FLOAT NOT NULL,
	LONGITUDE FLOAT NOT NULL,
	CONSTRAINT LOCATIONS_PK PRIMARY KEY (LOCATION_ID)
);

CREATE TABLE IF NOT EXISTS EVENTS (
    EVENT_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    USER_ID BIGINT NOT NULL,
    ANNOTATION CHARACTER VARYING NOT NULL,
    CATEGORY_ID BIGINT NOT NULL,
    DESCRIPTION CHARACTER VARYING NOT NULL,
    EVENT_DATE TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    LOCATION_ID BIGINT NOT NULL,
    PAID BOOLEAN NOT NULL,
    PARTICIPANT_LIMIT INTEGER NOT NULL,
    REQUEST_MODERATION BOOLEAN NOT NULL,
    TITLE VARCHAR(120) NOT NULL,
    CREATED_ON TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    PUBLISHED_ON TIMESTAMP WITHOUT TIME ZONE,
    STATE VARCHAR NOT NULL,
    CONSTRAINT EVENTS_PK PRIMARY KEY (EVENT_ID),
    CONSTRAINT EVENTS_USERS_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
    CONSTRAINT EVENTS_CATEGORIES_FK FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORIES(CATEGORY_ID),
    CONSTRAINT EVENTS_LOCATIONS_FK FOREIGN KEY (LOCATION_ID) REFERENCES LOCATIONS(LOCATION_ID)
);


CREATE TABLE IF NOT EXISTS COMPILATIONS (
    COMPILATION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    PINNED BOOLEAN NOT NULL,
    TITLE VARCHAR(50) UNIQUE NOT NULL,
    CONSTRAINT COMPILATIONS_PK PRIMARY KEY (COMPILATION_ID)
);

CREATE TABLE IF NOT EXISTS COMPILATIONS_EVENTS (
    COMPILATION_EVENT_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    EVENT_ID BIGINT NOT NULL,
    COMPILATION_ID BIGINT NOT NULL,
    CONSTRAINT COMPILATIONS_EVENTS_PK PRIMARY KEY (COMPILATION_EVENT_ID),
    CONSTRAINT COMPILATIONS_EVENTS_EVENTS_FK FOREIGN KEY (EVENT_ID) REFERENCES EVENTS(EVENT_ID),
    CONSTRAINT COMPILATIONS_EVENTS_COMPILATIONS_FK FOREIGN KEY (COMPILATION_ID) REFERENCES COMPILATIONS(COMPILATION_ID)
);

CREATE TABLE IF NOT EXISTS REQUESTS (
    REQUEST_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    EVENT_ID BIGINT NOT NULL,
    USER_ID BIGINT NOT NULL,
    STATUS VARCHAR NOT NULL,
    CREATED TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT REQUESTS_PK PRIMARY KEY (REQUEST_ID),
    CONSTRAINT REQUESTS_EVENTS_FK FOREIGN KEY (EVENT_ID) REFERENCES EVENTS(EVENT_ID),
    CONSTRAINT REQUESTS_USERS_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
    CONSTRAINT REQUEST_UNIQUE UNIQUE (EVENT_ID, USER_ID)
);

CREATE TABLE IF NOT EXISTS COMMENTS (
	COMMENT_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	TEXT VARCHAR(3000) NOT NULL,
	EVENT_ID BIGINT NOT NULL,
	USER_ID BIGINT NOT NULL,
	CREATED_TIME TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	CONSTRAINT COMMENTS_PK PRIMARY KEY (COMMENT_ID),
	CONSTRAINT COMMENTS_EVENTS_FK FOREIGN KEY (EVENT_ID) REFERENCES EVENTS(EVENT_ID),
    CONSTRAINT COMMENTS_USERS_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
);