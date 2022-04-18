
CREATE TABLE USERS (
                       ID IDENTITY NOT NULL PRIMARY KEY,
                       EMAIL VARCHAR2(100) NOT NULL,
                       PASS VARCHAR2(300) NOT NULL,
                       NAME VARCHAR2(100) NOT NULL,
                       STATUS VARCHAR2(1) NOT NULL

);

INSERT INTO USERS (EMAIL, PASS, NAME, STATUS) VALUES ('admin@email.com.br', '123999', 'Admin', 'A');