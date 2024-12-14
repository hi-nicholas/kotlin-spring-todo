CREATE TABLE party
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name           TEXT   NOT NULL,
    party_type     TEXT   NOT NULL,
    description    TEXT             DEFAULT NULL,
    eff_start_date DATE   NOT NULL,
    eff_end_date   DATE   NOT NULL  DEFAULT '9999-12-31',
    metadata       JSONB  NOT NULL  DEFAULT '{}',
    created_at     BIGINT NOT NULL,
    updated_at     BIGINT NOT NULL,
    deleted_at     BIGINT           DEFAULT NULL,
    is_active      BOOLEAN          DEFAULT TRUE
);

CREATE TABLE ledger_account
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parent_id      UUID             DEFAULT NULL,
    party_id       UUID   NOT NULL,
    name           TEXT   NOT NULL,
    balance_type   TEXT   NOT NULL,
    category       TEXT   NOT NULL,
    type           TEXT   NOT NULL,
    eff_start_date DATE   NOT NULL,
    eff_end_date   DATE   NOT NULL  DEFAULT '9999-12-31',
    metadata       JSONB  NOT NULL  DEFAULT '{}',
    created_at     BIGINT NOT NULL,
    updated_at     BIGINT NOT NULL,
    deleted_at     BIGINT           DEFAULT NULL,
    is_active      BOOLEAN          DEFAULT TRUE
);
CREATE UNIQUE INDEX idx_ledger_account_party ON ledger_account (party_id, name);
CREATE INDEX idx_ledger_account_party_type ON ledger_account (party_id, type);

CREATE TABLE ledger_account_balance
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id   UUID   NOT NULL,
    amount       BIGINT NOT NULL  DEFAULT 0,
    credits      BIGINT NOT NULL  DEFAULT 0,
    debits       BIGINT NOT NULL  DEFAULT 0,
    balance_date DATE   NOT NULL,
    created_at   BIGINT NOT NULL,
    updated_at   BIGINT NOT NULL,
    lock_version BIGINT NOT NULL  DEFAULT 0,
    CONSTRAINT fk_ledger_account_balance_account_id FOREIGN KEY (account_id) REFERENCES ledger_account (id)
);
CREATE UNIQUE INDEX idx_ledger_account_balance_date ON ledger_account_balance (account_id, balance_date DESC);

CREATE TABLE journal_entry
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event             TEXT   NOT NULL,
    type              TEXT   NOT NULL,
    credit_account_id UUID             DEFAULT NULL,
    debit_account_id  UUID             DEFAULT NULL,
    amount            BIGINT NOT NULL,
    transaction_date  DATE   NOT NULL,
    metadata          JSONB  NOT NULL  DEFAULT '{}',
    related_entry_id  UUID             DEFAULT NULL,
    created_at        BIGINT NOT NULL
);


INSERT INTO party (id, name, party_type, eff_start_date, created_at, updated_at)
VALUES ('96312501-59c5-488f-8754-ee164f06ae1d', 'John Smith', 'PARTICIPANT', '2024-01-01', 0, 0),
       ('6647543d-a2a1-4c06-92e8-fa82b5ddade5', 'Acme Co Plan', 'PLAN', '2024-01-01', 0, 0),
       ('a902d113-4441-40cf-b1cd-df020750f6ed', 'Matrix', 'CUSTODIAN', '2024-01-01', 0, 0);

INSERT INTO ledger_account(id, party_id, name, balance_type, type, category, eff_start_date, created_at, updated_at)
VALUES ('e9f48106-e77f-40db-ada6-0a4d106c67fb', '96312501-59c5-488f-8754-ee164f06ae1d', 'Clearing', 'DEBIT', 'CLEARING',
        'ASSET', '2024-01-01', 0, 0),
       ('ee44066d-23b0-42d4-9d8e-a128236ebac5', '96312501-59c5-488f-8754-ee164f06ae1d', 'Pre-Tax Contributions',
        'DEBIT', 'EE_PRETAX', 'ASSET', '2024-01-01', 0, 0),
       ('3a78e7e8-a8ee-48af-96fd-eb657a96ba0f', '96312501-59c5-488f-8754-ee164f06ae1d', 'Employer Contributions',
        'DEBIT', 'EE_MATCH', 'ASSET', '2024-01-01', 0, 0),
       ('82f5a759-ab73-4342-880d-87fc829124cf', '96312501-59c5-488f-8754-ee164f06ae1d', 'Invested Cash', 'CREDIT',
        'INVESTED_CASH', 'EXPENSE', '2024-01-01', 0, 0);

INSERT INTO ledger_account(id, party_id, name, balance_type, type, category, eff_start_date, created_at, updated_at)
VALUES ('a902d113-4441-40cf-b1cd-df020750f6ed', '6647543d-a2a1-4c06-92e8-fa82b5ddade5', 'Clearing', 'DEBIT', 'CLEARING',
        'ASSET', '2024-01-01', 0, 0),
       ('d7a5eb7b-0473-4c56-a992-c0143b0e5eb2', '6647543d-a2a1-4c06-92e8-fa82b5ddade5', 'Cash', 'DEBIT', 'CASH',
        'ASSET', '2024-01-01', 0, 0),
       ('5dd1fcd6-86ae-4ef1-b088-22687a33f9eb', '6647543d-a2a1-4c06-92e8-fa82b5ddade5', 'Deposit', 'DEBIT', 'DEPOSIT',
        'ASSET', '2024-01-01', 0, 0),
       ('413fdfe9-29c2-4144-b15e-453c047a264a', '6647543d-a2a1-4c06-92e8-fa82b5ddade5', 'Forfeiture', 'DEBIT',
        'FORFEITURE', 'EQUITY', '2024-01-01', 0, 0),
       ('99d8c5d4-ed74-4965-af09-8e3467355d2c', '6647543d-a2a1-4c06-92e8-fa82b5ddade5', 'Suspense', 'DEBIT', 'SUSPENSE',
        'ASSET', '2024-01-01', 0, 0),
       ('8c4da075-e35e-4717-97f0-4ebde3c1c221', '6647543d-a2a1-4c06-92e8-fa82b5ddade5', 'Adjustments', 'CREDIT',
        'ADJUSTMENTS', 'LIABILITY', '2024-01-01', 0, 0);

INSERT INTO ledger_account(id, party_id, name, balance_type, type, category, eff_start_date, created_at, updated_at)
VALUES ('0618e82a-b01b-43db-b8ef-67136540e001', 'a902d113-4441-40cf-b1cd-df020750f6ed', 'Clearing', 'DEBIT', 'CLEARING',
        'ASSET', '2024-01-01', 0, 0),
       ('ffc4c602-d7c0-4fb5-888f-d7f009529604', 'a902d113-4441-40cf-b1cd-df020750f6ed', 'Cash', 'DEBIT', 'CASH',
        'ASSET', '2024-01-01', 0, 0),
       ('02df5cc6-7b6f-48a3-8a57-a4a4184fba08', 'a902d113-4441-40cf-b1cd-df020750f6ed', 'Deposit', 'DEBIT', 'DEPOSIT',
        'ASSET', '2024-01-01', 0, 0),
       ('d5b4e54f-0546-4014-add8-324b89cdad41', 'a902d113-4441-40cf-b1cd-df020750f6ed', 'Suspense', 'DEBIT', 'SUSPENSE',
        'ASSET', '2024-01-01', 0, 0),
       ('ff560bad-e124-4106-8cfc-36a50aa0ac0e', 'a902d113-4441-40cf-b1cd-df020750f6ed', 'Adjustments', 'CREDIT',
        'ADJUSTMENTS', 'LIABILITY', '2024-01-01', 0, 0);

INSERT INTO ledger_account_balance (account_id, balance_date, created_at, updated_at, lock_version)
    (SELECT id             AS account_id,
            eff_start_date AS balance_date,
            created_at,
            updated_at,
            1 AS lock_version
     FROM ledger_account
     WHERE NOT (id = '02df5cc6-7b6f-48a3-8a57-a4a4184fba08'));

INSERT INTO ledger_account_balance(account_id, amount, debits, balance_date, created_at, updated_at, lock_version)
VALUES ('02df5cc6-7b6f-48a3-8a57-a4a4184fba08', 500000000000, 500000000000, '2024-01-01', 0, 0, 1);


