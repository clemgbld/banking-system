CREATE TABLE account
(
    id          VARCHAR(255)   NOT NULL,
    customer_id VARCHAR(255)   NOT NULL,
    iban        VARCHAR(255)   NOT NULL,
    balance     DECIMAL(10, 2) NOT NULL,
    created_on  TIMESTAMP      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (customer_id),
    UNIQUE (iban)
);

CREATE TABLE beneficiary
(
    id         VARCHAR(255) NOT NULL,
    iban       VARCHAR(255) NOT NULL,
    bic        VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    account_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE TABLE transaction
(
    id                 VARCHAR(255)   NOT NULL,
    creation_date      TIMESTAMP      NOT NULL,
    transaction_amount DECIMAL(10, 2) NOT NULL,
    account_identifier VARCHAR(255)   NOT NULL,
    bic                VARCHAR(255)   NOT NULL,
    account_name       VARCHAR(255)   NOT NULL,
    reason             VARCHAR(255),
    account_id         VARCHAR(255)   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES account (id)
);