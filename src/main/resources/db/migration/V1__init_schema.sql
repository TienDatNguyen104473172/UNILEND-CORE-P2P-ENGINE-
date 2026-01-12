-- 1 users table (users)
create table users(
    id bigserial primary key, -- bigserial use for business transactions
    email varchar(100) not null unique,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP --current local time
);

-- 2 wallet table (wallets)
create table wallets
(
    id      BIGSERIAL PRIMARY KEY,
    user_id bigint not null unique,      -- user only have 1 wallet
    balance DECIMAL(19, 2) DEFAULT 0.00, -- Use DECIMAL for currency (Fintech standard), If no value is passed to the balance when insert, the database will automatically assign a value of 0.00.
    version INT DEFAULT 0, -- Used for Optimistic Locking (anti-dispute locking) / avoiding race condition or lost update
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 3 ledger transactions table (The ledger - The heart of the system)
-- All cash transactions must be recorded here first
CREATE TABLE ledger_transactions
(
    id             BIGSERIAL PRIMARY KEY,
    wallet_id      BIGINT         NOT NULL,
    amount         DECIMAL(19, 2) NOT NULL, -- The amount (+) is for depositing/receiving, - is for withdrawing/paying.
    type           VARCHAR(50)    NOT NULL, -- Transaction types: Deposit, Withdrawal, Investment...
    reference_id   BIGINT,                  -- ID of the relevant entity (e.g., loan ID)
    reference_type VARCHAR(50),             -- Type of client: Loan, Investment...
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets (id)
);

-- Create an index for faster searching.
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_ledger_wallet ON ledger_transactions(wallet_id);