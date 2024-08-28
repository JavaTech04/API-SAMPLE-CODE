CREATE TABLE tbl_user
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    date_of_birth DATE         NOT NULL,
    gender        ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    phone         VARCHAR(255) DEFAULT NULL,
    email         VARCHAR(255) DEFAULT NULL,
    username      VARCHAR(255) NOT NULL,
    password      VARCHAR(255) NOT NULL,
    status        ENUM('ACTIVE', 'INACTIVE', 'NONE') NOT NULL,
    type          ENUM('OWNER', 'ADMIN', 'USER') NOT NULL,
    created_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

-- Table `tbl_address` definition

CREATE TABLE tbl_address
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    apartment_number VARCHAR(255) DEFAULT NULL,
    floor            VARCHAR(255) DEFAULT NULL,
    building         VARCHAR(255) DEFAULT NULL,
    street_number    VARCHAR(255) DEFAULT NULL,
    street           VARCHAR(255) DEFAULT NULL,
    city             VARCHAR(255) DEFAULT NULL,
    country          VARCHAR(255) DEFAULT NULL,
    address_type     INT          DEFAULT NULL,
    user_id          BIGINT       DEFAULT NULL,
    created_at       TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at       TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    FOREIGN KEY (user_id) REFERENCES tbl_user (id)
);