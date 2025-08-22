INSERT INTO cheer_image (cheer_id, image_key, order_index, created_at)
SELECT c.id,
       CONCAT('cheer/', c.id, '/', REGEXP_REPLACE(c.image_key, '.*\/', '')),
       1,
       c.created_at
FROM cheer c
WHERE c.image_key IS NOT NULL
  AND c.image_key != ''
  AND NOT EXISTS (SELECT 1
                  FROM cheer_image ci
                  WHERE ci.cheer_id = c.id
                    AND ci.order_index = 1);

INSERT INTO story_image (story_id, image_key, order_index, created_at)
SELECT s.id,
       CONCAT('story/', s.id, '/', REGEXP_REPLACE(s.image_key, '.*\/', '')),
       1,
       s.created_at
FROM story s
WHERE s.image_key IS NOT NULL
  AND s.image_key != ''
  AND NOT EXISTS (SELECT 1
                  FROM story_image si
                  WHERE si.story_id = s.id
                    AND si.order_index = 1);

ALTER TABLE cheer RENAME COLUMN image_key TO _deprecated_image_key;
ALTER TABLE story RENAME COLUMN image_key TO _deprecated_image_key;
