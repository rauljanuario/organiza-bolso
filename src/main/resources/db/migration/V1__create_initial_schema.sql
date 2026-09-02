CREATE TABLE IF NOT EXISTS users (

    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE IF NOT EXISTS categories (

    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL, CHECK ( type in ('INCOME', 'EXPENSE') ),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_categories_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_category_name_per_user UNIQUE (name, user_id)

);

CREATE TABLE IF NOT EXISTS category_rules (

    id BIGSERIAL PRIMARY KEY,
    keyword VARCHAR(50) NOT NULL UNIQUE,
    category_id BIGINT NOT NULL,
    priority INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_rules_category FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE CASCADE

);

CREATE TABLE IF NOT EXISTS statements_imports (

    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    format VARCHAR(10) NOT NULL CHECK ( format in ('CSV', 'OFX') ),
    status VARCHAR(20) NOT NULL CHECK ( status in ('PROCESSING', 'COMPLETED', 'FAILED') ),
    user_id BIGINT NOT NULL,
    imported_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_imports_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE

);

CREATE TABLE IF NOT EXISTS transactions (

    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    transaction_date DATE NOT NULL,
    category_id BIGINT,
    import_id BIGINT,
    user_id BIGINT NOT NULL,
    manually_categorized BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transactions_category FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_transactions_import FOREIGN KEY(import_id) REFERENCES statements_imports(id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE

);