CREATE TABLE `cheer_tag`
(
    `id`       BIGINT      NOT NULL AUTO_INCREMENT,
    `cheer_id` BIGINT      NOT NULL,
    `name`     VARCHAR(63) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`cheer_id`) REFERENCES `cheer` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_cheer_tag_cheer_id_name` (`cheer_id`, `name`)
);

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

INSERT INTO cheer_image (cheer_id, image_key, order_index, created_at)
SELECT c.id,
       CONCAT('cheer/', c.id, '/', c.image_key),
       1,
       c.created_at
FROM cheer c
WHERE c.image_key IS NOT NULL
  AND c.image_key != '';

INSERT INTO story_image (story_id, image_key, order_index, created_at)
SELECT s.id,
       CONCAT('story/', s.id, '/', s.image_key),
       1,
       s.created_at
FROM story s
WHERE s.image_key IS NOT NULL
  AND s.image_key != '';

ALTER TABLE cheer RENAME COLUMN image_key TO _deprecated_image_key;
ALTER TABLE story RENAME COLUMN image_key TO _deprecated_image_key;
