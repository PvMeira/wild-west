create table APPLICATION_VARS(
                      name varchar2(100) not null primary key,
                      value varchar2(100) not null,
                      STATUS VARCHAR2(1) NOT NULL
);


INSERT INTO APPLICATION_VARS (name, value, STATUS) VALUES ('SUSPECT_TRANSACTION','100000.00', 'A');
INSERT INTO APPLICATION_VARS (name, value, STATUS) VALUES ('SUSPECT_ACCOUNT','1000000.00', 'A');
INSERT INTO APPLICATION_VARS (name, value, STATUS) VALUES ('SUSPECT_AGENCY','1000000000.00', 'A');