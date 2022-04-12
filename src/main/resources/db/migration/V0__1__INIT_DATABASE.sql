
CREATE TABLE TRANSACTIONS_PACKAGE (
                              package_date   DATE PRIMARY KEY NOT NULL,
                              total_transactions number  NULL,
                              total_error_transactions number  NULL,
                              process_start_date DATETIME NOT NULL,
                              process_end_date DATETIME NULL

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