CREATE TABLE `cheer_image`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `cheer_id`     BIGINT       NOT NULL,
    `image_key`    VARCHAR(511) NOT NULL,
    `order_index`  BIGINT       NOT NULL,
    `content_type` VARCHAR(255) NULL,
    `file_size`    BIGINT       NULL,
    `created_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`cheer_id`) REFERENCES `cheer` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_cheer_id_order_index` (`cheer_id`, `order_index`)
);

CREATE TABLE `story_image`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `story_id`     BIGINT       NOT NULL,
    `image_key`    VARCHAR(511) NOT NULL,
    `order_index`  BIGINT       NOT NULL,
    `content_type` VARCHAR(255) NULL,
    `file_size`    BIGINT       NULL,
    `created_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`story_id`) REFERENCES `story` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_story_id_order_index` (`story_id`, `order_index`)
);
