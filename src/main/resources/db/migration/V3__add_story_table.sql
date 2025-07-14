CREATE TABLE `story`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `member_id`   BIGINT       NOT NULL,
    `store_id`    BIGINT       NOT NULL,
    `description` TEXT         NOT NULL,
    `image_key`   VARCHAR(511) NOT NULL,
    `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);
