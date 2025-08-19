package eatda.client.file;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
public class FileClient {

    private static final int THREAD_POOL_SIZE = 5; // TODO 비동기 병렬처리 개선
    private final S3Client s3Client;
    private final String bucket;
    private final S3Presigner s3Presigner;
    private final ExecutorService executorService;

    public FileClient(S3Client s3Client,
                      @Value("${spring.cloud.aws.s3.bucket}") String bucket,
                      S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.s3Presigner = s3Presigner;
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
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
        List<CompletableFuture<String>> futures = tempImageKeys.stream()
                .map(tempImageKey -> CompletableFuture.supplyAsync(() -> {
                    String fileName = extractFileName(tempImageKey);
                    String newPermanentKey = domainName + "/" + domainId + "/" + fileName;
                    try {
                        copyObject(tempImageKey, newPermanentKey);
                        deleteObject(tempImageKey);
                        return newPermanentKey;
                    } catch (Exception e) { //TODO 근본 예외 추가 필요
                        throw new BusinessException(BusinessErrorCode.FAIL_TEMP_IMAGE_PROCESS);
                    }
                }, executorService))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join) // TODO 일부 파일 에러에도 처리하도록 개선
                .toList();
    }

    private String extractFileName(String fullKey) {
        int index = fullKey.lastIndexOf('/');
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
