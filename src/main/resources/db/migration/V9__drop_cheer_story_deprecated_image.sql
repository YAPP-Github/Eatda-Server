SET @col_exists := (SELECT COUNT(*)
                    FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE TABLE_SCHEMA = DATABASE()
                      AND TABLE_NAME = 'cheer'
                      AND COLUMN_NAME = '_deprecated_image_key');

SET @sql := IF(@col_exists > 0,
               'ALTER TABLE cheer DROP COLUMN _deprecated_image_key',
               'SELECT "Column cheer._deprecated_image_key does not exist, skip";');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


SET @col_exists := (SELECT COUNT(*)
                    FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE TABLE_SCHEMA = DATABASE()
                      AND TABLE_NAME = 'story'
                      AND COLUMN_NAME = '_deprecated_image_key');

SET @sql := IF(@col_exists > 0,
               'ALTER TABLE story DROP COLUMN _deprecated_image_key',
               'SELECT "Column story._deprecated_image_key does not exist, skip";');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
