#!/bin/bash
set -e

BACKUP_DIR="/home/ec2-user/mysql"
S3_BUCKET="s3://eatda-storage-dev/backup/mysql/"
TIMESTAMP=$(date +%Y-%m-%d-%H%M%S)
ARCHIVE_PATH="${BACKUP_DIR}/mysql-backup-${TIMESTAMP}.sql"

MYSQL_URL=$(aws ssm get-parameter --name "/dev/MYSQL_URL" --with-decryption --query "Parameter.Value" --output text)
MYSQL_USER=$(aws ssm get-parameter --name "/dev/MYSQL_USER_NAME" --with-decryption --query "Parameter.Value" --output text)
MYSQL_PASSWORD=$(aws ssm get-parameter --name "/dev/MYSQL_PASSWORD" --with-decryption --query "Parameter.Value" --output text)

HOST=$(echo "$MYSQL_URL" | sed -E 's|jdbc:mysql://([^:/]+):([0-9]+)/([^?]+).*|\1|')
PORT=$(echo "$MYSQL_URL" | sed -E 's|jdbc:mysql://([^:/]+):([0-9]+)/([^?]+).*|\2|')
DB_NAME=$(echo "$MYSQL_URL" | sed -E 's|jdbc:mysql://([^:/]+):([0-9]+)/([^?]+).*|\3|')

if mysqldump -h "$HOST" -P "$PORT" -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" "$DB_NAME" > "$ARCHIVE_PATH"; then
  echo "[INFO] MySQL backup successful: $ARCHIVE_PATH"

  if aws s3 cp "$ARCHIVE_PATH" "$S3_BUCKET"; then
    echo "[INFO] Upload successful: $ARCHIVE_PATH -> $S3_BUCKET"
    rm "$ARCHIVE_PATH"
  else
    echo "[ERROR] Failed to upload backup to S3." >&2
    exit 1
  fi

else
  echo "[ERROR] mysqldump failed." >&2
  exit 1
fi
