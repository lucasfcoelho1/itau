CREATE TABLE country
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(255) NOT NULL UNIQUE,
    region           VARCHAR(255),
    total_population BIGINT,
    flag_url         VARCHAR(512)
);

CREATE TABLE dog
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    breed        VARCHAR(255) NOT NULL,
    description TEXT,
    country_id  BIGINT       NOT NULL UNIQUE,
    FOREIGN KEY (country_id) REFERENCES country (id) ON DELETE CASCADE
);