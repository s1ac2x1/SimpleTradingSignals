CREATE TABLE IF NOT EXISTS address (
    id VARCHAR NOT NULL PRIMARY KEY,
    address_type TEXT NOT NULL DEFAULT 'HOME',
    street_name VARCHAR(255),
    street_number VARCHAR(16),
    second_address_line VARCHAR(255),
    city VARCHAR(128) NOT NULL,
    postal_code VARCHAR(16) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    modified_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS person (
    id VARCHAR NOT NULL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    address_id VARCHAR(36) REFERENCES address,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    modified_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version INT DEFAULT 0
);

CREATE INDEX address_streenname_streetnumber_idx ON address(street_name, street_number);