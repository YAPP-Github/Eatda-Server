package eatda.client.file;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Component
public class FileClient {

    private static final String PATH_DELIMITER = "/";
    private final S3Client s3Client;
    private final String bucket;
    private final S3Presigner s3Presigner;

    public FileClient(S3Client s3Client,
                      @Value("${spring.cloud.aws.s3.bucket}") String bucket,
                      S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.s3Presigner = s3Presigner;
    }

    public String generateUploadPresignedUrl(String fileKey, Duration signatureDuration) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(signatureDuration)
                .build();

        try {
            return s3Presigner.presignPutObject(presignRequest).url().toString();
        } catch (Exception exception) {
            throw new BusinessException(BusinessErrorCode.PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    public List<String> moveTempFilesToPermanent(String domainName, long domainId, List<String> tempImageKeys) {
        List<String> successKeys = new ArrayList<>();

        try {
            for (String tempKey : tempImageKeys) {
                String fileName = extractFileName(tempKey);
                String newPermanentKey = domainName + PATH_DELIMITER + domainId + PATH_DELIMITER + fileName;

                copyObject(tempKey, newPermanentKey);
                deleteObject(tempKey);

                successKeys.add(newPermanentKey);
            }
            return successKeys;
        } catch (SdkException sdkException) {
            log.error("S3 파일 이동 중 실패. 롤백 수행. successKeys={}", successKeys, sdkException);
            deleteFiles(successKeys);
            throw new BusinessException(BusinessErrorCode.FAIL_TEMP_IMAGE_PROCESS);
        }
    }

    public void deleteFiles(List<String> keys) {
        if (keys.isEmpty()) {
            return;
        }
        keys.forEach(this::deleteObject);
    }

    private String extractFileName(String fullKey) {
        int index = fullKey.lastIndexOf(PATH_DELIMITER);
        return index == -1 ? fullKey : fullKey.substring(index + 1);
    }

    private void copyObject(String sourceKey, String destinationKey) {
        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(sourceKey)
                .destinationBucket(bucket)
                .destinationKey(destinationKey)
                .build();
        s3Client.copyObject(copyReq);
    }

    private void deleteObject(String key) {
        DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(deleteReq);
    }
}
