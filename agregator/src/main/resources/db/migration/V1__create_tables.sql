CREATE TABLE subscribers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone_number VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE call (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    call_type VARCHAR(2) NOT NULL,
    caller BIGINT NOT NULL,
    receiver BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    FOREIGN KEY (caller) REFERENCES subscribers(id),
    FOREIGN KEY (receiver) REFERENCES subscribers(id)
);