import boto3
import json
import logging
import os
import pymysql
from botocore.exceptions import ClientError

# 로깅 설정
logger = logging.getLogger()
logger.setLevel(logging.INFO)

# Boto3 클라이언트
s3_client = boto3.client('s3')
secrets_manager_client = boto3.client('secretsmanager')

# Terraform에서 주입하는 환경 변수
SOURCE_BUCKET_FOR_CLONE = os.environ.get('TEST_ORIGIN_SOURCE_BUCKET')
CLONED_BUCKET_FOR_TEST = os.environ.get('TEST_TARGET_BUCKET')


def get_db_secret(secret_arn):
    """Secrets Manager에서 DB 접속 정보를 가져오는 함수"""
    try:
        response = secrets_manager_client.get_secret_value(SecretId=secret_arn)
        return json.loads(response['SecretString'])
    except Exception as e:
        logger.error(f"Error getting secret from Secrets Manager: {e}")
        raise


def preprocess_clone_s3_for_test(event):
    """
    [작업 1: 전처리] 테스트를 위해 원본 S3 버킷의 모든 객체를
    임시 복제 버킷으로 복사합니다.
    """
    source = SOURCE_BUCKET_FOR_CLONE
    target = CLONED_BUCKET_FOR_TEST
    logger.info(f"Starting S3 clone from source bucket '{source}' to target bucket '{target}'")

    if not source or not target:
        raise ValueError("Environment variables for source or target bucket are not set.")

    try:
        paginator = s3_client.get_paginator('list_objects_v2')
        for page in paginator.paginate(Bucket=source):
            if 'Contents' not in page:
                continue

            for obj in page['Contents']:
                copy_source = {'Bucket': source, 'Key': obj['Key']}
                s3_client.copy_object(CopySource=copy_source, Bucket=target, Key=obj['Key'])

        logger.info(f"Successfully cloned all objects from '{source}' to '{target}'.")
        return {"status": "SUCCESS", "message": "Preprocessing (S3 Clone) completed."}

    except Exception as e:
        logger.error(f"An unexpected error occurred during S3 cloning: {e}")
        raise


def move_s3_object(bucket, old_key, new_key):
    """S3 객체를 복사하고 원본을 삭제하는 헬퍼 함수. 동일 키는 스킵."""
    if old_key == new_key:
        logger.info(f"Old key and new key are identical ('{old_key}'). Skipping move.")
        return False

    try:
        copy_source = {'Bucket': bucket, 'Key': old_key}
        s3_client.copy_object(CopySource=copy_source, Bucket=bucket, Key=new_key)
        s3_client.delete_object(Bucket=bucket, Key=old_key)
        logger.info(f"Successfully moved '{old_key}' to '{new_key}' in bucket '{bucket}'.")
        return True
    except ClientError as e:
        error_code = e.response.get('Error', {}).get('Code')
        if error_code in ('NoSuchKey', '404', 'NotFound'):
            logger.warning(f"Source object '{old_key}' not found in bucket '{bucket}'. Skipping.")
            return False
        logger.error(f"Failed to move object '{old_key}' to '{new_key}': {e}")
        raise
    except Exception as e:
        logger.error(f"Failed to move object '{old_key}' to '{new_key}': {e}")
        raise


def postprocess_realign_data_from_db(event):
    """
    [작업 2: 후처리] 마이그레이션된 DB를 직접 읽어 S3 객체를 재배치합니다.
    """
    target_bucket = event.get('target_bucket_for_realignment')
    db_endpoint = event.get('db_endpoint')
    db_username = event.get('db_username')
    db_password = event.get('db_password')

    if not all([target_bucket, db_endpoint, db_username, db_password]):
        raise ValueError("Payload must include target_bucket, db_endpoint, db_username, db_password.")

    logger.info(f"Starting data realignment for bucket '{target_bucket}' using DB at '{db_endpoint}'.")

    connection = None
    moved_count = 0

    try:
        connection = pymysql.connect(
            host=db_endpoint,
            user=db_username,
            password=db_password,
            database='eatda',
            cursorclass=pymysql.cursors.DictCursor
        )
        logger.info("Successfully connected to the database.")

        with connection.cursor() as cursor:
            # Cheer 이미지 매핑
            logger.info("Querying for 'cheer' image realignment tasks...")
            sql_cheer = """
                        SELECT c._deprecated_image_key as old_key, ci.image_key as new_key
                        FROM cheer c
                                 JOIN cheer_image ci ON c.id = ci.cheer_id
                        WHERE c._deprecated_image_key IS NOT NULL
                          AND c._deprecated_image_key != ''
                          AND ci.order_index = 1
                        """
            cursor.execute(sql_cheer)
            cheer_tasks = cursor.fetchall()
            logger.info(f"Found {len(cheer_tasks)} 'cheer' image(s) to move.")
            for task in cheer_tasks:
                if move_s3_object(target_bucket, task['old_key'], task['new_key']):
                    moved_count += 1

            # Story 이미지 매핑
            logger.info("Querying for 'story' image realignment tasks...")
            sql_story = """
                        SELECT s._deprecated_image_key as old_key, si.image_key as new_key
                        FROM story s
                                 JOIN story_image si ON s.id = si.story_id
                        WHERE s._deprecated_image_key IS NOT NULL
                          AND s._deprecated_image_key != ''
                          AND si.order_index = 1
                        """
            cursor.execute(sql_story)
            story_tasks = cursor.fetchall()
            logger.info(f"Found {len(story_tasks)} 'story' image(s) to move.")
            for task in story_tasks:
                if move_s3_object(target_bucket, task['old_key'], task['new_key']):
                    moved_count += 1

    finally:
        if connection:
            connection.close()
            logger.info("Database connection closed.")

    logger.info(f"Data realignment task completed. Total objects moved: {moved_count}.")
    return {"status": "SUCCESS", "message": f"Moved {moved_count} objects."}


def lambda_handler(event, context):
    """
    메인 핸들러: 워크플로우에서 전달된 'task' 값에 따라 적절한 작업을 호출합니다.
    """
    task = event.get('task')
    logger.info(f"Received task: '{task}'")

    if task == 'preprocess':
        return preprocess_clone_s3_for_test(event)
    elif task == 'postprocess':
        return postprocess_realign_data_from_db(event)
    else:
        error_message = f"Unknown or invalid task specified: '{task}'. Must be 'preprocess' or 'postprocess'."
        logger.error(error_message)
        raise ValueError(error_message)
