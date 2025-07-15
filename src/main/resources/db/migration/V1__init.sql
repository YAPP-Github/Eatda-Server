CREATE TABLE `member`
(
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `email`            VARCHAR(255) NOT NULL UNIQUE,
    `social_id`        VARCHAR(255) NOT NULL UNIQUE,
    `nickname`         VARCHAR(255) NULL,
    `phone_number`     VARCHAR(255) NULL,
    `opt_in_marketing` BOOLEAN      NULL,
    `created_at`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `store` (
     `id`                 BIGINT       NOT NULL AUTO_INCREMENT,
     `kakao_id`           VARCHAR(255) NOT NULL UNIQUE,
     `category`           VARCHAR(50)  NOT NULL,
     `phone_number`       VARCHAR(255) NOT NULL,
     `name`               VARCHAR(255) NOT NULL,
     `place_url`          VARCHAR(255) NOT NULL,
     `road_address`       VARCHAR(255) NOT NULL,
     `lot_number_address` VARCHAR(255) NOT NULL,
     `latitude`           DOUBLE       NOT NULL,
     `longitude`          DOUBLE       NOT NULL,
     `created_at`         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`)
);

