CREATE TABLE `cheer_tag`
(
    `id`       BIGINT      NOT NULL AUTO_INCREMENT,
    `cheer_id` BIGINT      NOT NULL,
    `name`     VARCHAR(63) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`cheer_id`) REFERENCES `cheer` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_cheer_tag_cheer_id_name` (`cheer_id`, `name`)
);
