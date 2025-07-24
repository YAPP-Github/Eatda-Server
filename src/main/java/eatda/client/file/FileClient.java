package eatda.client.file;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
public class FileClient {

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

    public String upload(MultipartFile file, String fileKey) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return fileKey;
        } catch (Exception exception) {
            throw new BusinessException(BusinessErrorCode.FILE_UPLOAD_FAILED);
        }
        //  TODO 발생 예외 별로 처리하기
        // InvalidRequestException, InvalidWriteOffsetException, TooManyPartsException, EncryptionTypeMismatchException,
        // AwsServiceException, SdkClientException, S3Exception
    }

    public String getPreSignedUrl(String fileKey, Duration signatureDuration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(signatureDuration)
                .build();

        try {
            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception exception) {
            throw new BusinessException(BusinessErrorCode.PRESIGNED_URL_GENERATION_FAILED);
        }
    }
}
