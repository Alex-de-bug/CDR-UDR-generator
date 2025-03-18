CREATE TABLE subscribers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone_number VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE call (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    call_type VARCHAR(2) NOT NULL,
    caller_number VARCHAR(255) NOT NULL,
    receiver_number VARCHAR(255) NOT NULL,
    start_time VARCHAR(255) NOT NULL,
    end_time VARCHAR(255) NOT NULL
);