CREATE TABLE `story`
(
    `id`                       BIGINT       NOT NULL AUTO_INCREMENT,
    `member_id`                BIGINT       NOT NULL,
    `store_kakao_id`           VARCHAR(255) NOT NULL,
    `store_name`               VARCHAR(255) NOT NULL,
    `store_road_address`       VARCHAR(255) NOT NULL,
    `store_lot_number_address` VARCHAR(255) NOT NULL,
    `store_category`           VARCHAR(50)  NOT NULL,
    `description`              TEXT         NULL,
    `image_key`                VARCHAR(511) NOT NULL,
    `created_at`               TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);
