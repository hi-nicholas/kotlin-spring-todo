CREATE TABLE lab_day
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id   UUID   NOT NULL,
    amount       BIGINT NOT NULL  DEFAULT 0,
    credits      BIGINT NOT NULL  DEFAULT 0,
    debits       BIGINT NOT NULL  DEFAULT 0,
    year         INT    NOT NULL,
    month        INT    NOT NULL,
    day          INT    NOT NULL,
    CONSTRAINT uq_lab_day_date UNIQUE (account_id, year, month, day)
);

CREATE TABLE lab_month
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id   UUID   NOT NULL,
    amount       BIGINT NOT NULL  DEFAULT 0,
    credits      BIGINT NOT NULL  DEFAULT 0,
    debits       BIGINT NOT NULL  DEFAULT 0,
    year         INT    NOT NULL,
    month        INT    NOT NULL,
    CONSTRAINT uq_lab_month_date UNIQUE (account_id, year, month)
);


CREATE TABLE lab_quarter
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id   UUID   NOT NULL,
    amount       BIGINT NOT NULL  DEFAULT 0,
    credits      BIGINT NOT NULL  DEFAULT 0,
    debits       BIGINT NOT NULL  DEFAULT 0,
    year         INT    NOT NULL,
    quarter      INT    NOT NULL,
    CONSTRAINT uq_lab_quarter_date UNIQUE (account_id, year, quarter)
);


CREATE TABLE lab_year
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id   UUID   NOT NULL,
    amount       BIGINT NOT NULL  DEFAULT 0,
    credits      BIGINT NOT NULL  DEFAULT 0,
    debits       BIGINT NOT NULL  DEFAULT 0,
    year         INT    NOT NULL,
    CONSTRAINT uq_lab_year_date UNIQUE (account_id, year)
);

INSERT INTO lab_year (account_id, amount, debits, year)
VALUES ('02df5cc6-7b6f-48a3-8a57-a4a4184fba08', 500000000000, 500000000000, 2024);

INSERT INTO lab_quarter (account_id, amount, debits, year, quarter)
VALUES ('02df5cc6-7b6f-48a3-8a57-a4a4184fba08', 500000000000, 500000000000, 2024, 1);

INSERT INTO lab_month (account_id, amount, debits, year, month)
VALUES ('02df5cc6-7b6f-48a3-8a57-a4a4184fba08', 500000000000, 500000000000, 2024, 1);

INSERT INTO lab_day (account_id, amount, debits, year, month, day)
VALUES ('02df5cc6-7b6f-48a3-8a57-a4a4184fba08', 500000000000, 500000000000, 2024, 1, 1);
