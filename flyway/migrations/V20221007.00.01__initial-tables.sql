CREATE TABLE IF NOT EXISTS testings (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR,
    task_blocks TEXT,
    sl_strategy TEXT,
    tp_strategy TEXT,
    balance FLOAT8,
    successful_ratio FLOAT8,
    loss_ratio FLOAT8,
    all_positions_count INTEGER,
    profitable_positions_count INTEGER,
    loss_positions_count INTEGER,
    min_position_duration_seconds INT8,
    average_position_duration_seconds FLOAT8,
    max_position_duration_seconds INT8,
    min_profit FLOAT8,
    avg_profit FLOAT8,
    max_profit FLOAT8,
    min_loss FLOAT8,
    avg_loss FLOAT8,
    max_loss FLOAT8,
    total_Loss FLOAT8,
    total_profit FLOAT8,
    average_roi FLOAT8
);

CREATE TABLE IF NOT EXISTS signal_results (
    id SERIAL PRIMARY KEY,
    testings_id INT REFERENCES testings,
    result TEXT
);