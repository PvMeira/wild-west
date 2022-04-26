create table users(
                      username varchar_ignorecase(100) not null primary key,
                      password varchar_ignorecase(100) not null,
                      enabled boolean not null,
                      NAME VARCHAR2(100) NOT NULL,
                      STATUS VARCHAR2(1) NOT NULL
);
INSERT INTO users (username, password, enabled,NAME, STATUS) VALUES ('admin@email.com.br', '$2a$10$XZquNRXi1hRHbVZgz1MDQutlh1Fn56a4HHeDPb4R3RtquDG9Dipf.', true, 'Admin', 'A');
create table authorities (
                             username varchar_ignorecase(50) not null,
                             authority varchar_ignorecase(50) not null,
                             constraint fk_authorities_users foreign key(username) references users(username)
);

INSERT INTO authorities (USERNAME, AUTHORITY) VALUES ('admin@email.com.br', 'ROLE_USER');
INSERT INTO authorities (USERNAME, AUTHORITY) VALUES ('admin@email.com.br', 'ROLE_ADMIN');
create unique index ix_auth_username on authorities (username,authority);

CREATE TABLE TRANSACTIONS_PACKAGE (
                              package_date   DATE PRIMARY KEY NOT NULL,
                              total_transactions number  NULL,
                              total_error_transactions number  NULL,
                              process_start_date DATETIME NOT NULL,
                              process_end_date DATETIME NULL,
                              username varchar_ignorecase(100) not null,
                              FOREIGN KEY (username) REFERENCES users(username)


);

CREATE TABLE TRANSACTIONS (
                               ID IDENTITY NOT NULL PRIMARY KEY,
                               origin_bank_name VARCHAR NOT NULL,
                               origin_bank_agency VARCHAR NOT NULL,
                               origin_account VARCHAR NOT NULL,
                               destiny_bank_name VARCHAR NOT NULL,
                               destiny_bank_agency  VARCHAR NOT NULL,
                               destiny_account VARCHAR NOT NULL,
                               transaction_value numeric(18,2) NOT NULL,
                               date_of_transaction VARCHAR NULL,
                               transactional_package_date DATETIME NOT NULL ,
                               FOREIGN KEY (transactional_package_date) REFERENCES TRANSACTIONS_PACKAGE(package_date)



);