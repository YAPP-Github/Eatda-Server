#!/bin/bash
set -e

LOG_DIR="/home/ec2-user/logs/eatda"
S3_BUCKET="s3://eatda-storage-prod/backup/logs/"
TIMESTAMP=$(date +%Y-%m-%d-%H%M%S)
ARCHIVE_PATH="/tmp/eatda-logs-${TIMESTAMP}.tar.gz"

if [ ! -d "$LOG_DIR" ]; then
  echo "[ERROR] Log directory does not exist: $LOG_DIR" >&2
  exit 1
fi

tar -czf "$ARCHIVE_PATH" -C "$LOG_DIR" .

if aws s3 cp "$ARCHIVE_PATH" "$S3_BUCKET"; then
  echo "[INFO] Upload successful: $ARCHIVE_PATH -> $S3_BUCKET"

  find "$LOG_DIR" -type f -name "*.log" -delete
  echo "[INFO] Old log files deleted from $LOG_DIR"

  rm "$ARCHIVE_PATH"
else
  echo "[ERROR] Failed to upload archive to S3." >&2
  exit 1
fi
