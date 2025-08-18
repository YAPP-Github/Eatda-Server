package eatda.client.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

class FileClientTest {

    private S3Client s3Client;
    private String bucket;
    private S3Presigner s3Presigner;
    private FileClient fileClient;

    @BeforeEach
    void setUp() {
        this.s3Client = mock(S3Client.class);
        this.bucket = "test-bucket";
        this.s3Presigner = mock(S3Presigner.class);
        this.fileClient = new FileClient(s3Client, bucket, s3Presigner);
    }

    @Nested
    class GenerateUploadPresignedUrl {

        @Test
        void 주어진_파일_Key에_대해_업로드용_사전_서명된_URL을_반환한다() throws MalformedURLException {
            String fileKey = "test-file-key.jpg";
            String expected = "https://example.com/test-file-key.jpg";
            doReturn(mockPresignedRequest(expected)).when(s3Presigner).presignPutObject(any(PutObjectPresignRequest.class));

            String actual = fileClient.generateUploadPresignedUrl(fileKey, Duration.ofMinutes(10));

            assertThat(actual).isEqualTo(expected);
        }

        private PresignedPutObjectRequest mockPresignedRequest(String url) throws MalformedURLException {
            PresignedPutObjectRequest request = mock(PresignedPutObjectRequest.class);
            doReturn(new URL(url)).when(request).url();
            return request;
        }

        @Test
        void 문제가_생길_경우_서비스_에러_처리를_한다() {
            String fileKey = "test-file-key.jpg";
            doThrow(SdkClientException.create("Presigned URL generation failed"))
                    .when(s3Presigner).presignPutObject(any(PutObjectPresignRequest.class));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> fileClient.generateUploadPresignedUrl(fileKey, Duration.ofMinutes(10)));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    @Nested
    class MoveTempFilesToPermanent {

        @Test
        void 임시_파일들을_영구_위치로_이동한다() {
            String domainName = "cheer";
            long domainId = 123L;
            List<String> tempImageKeys = List.of("temp1.jpg", "temp2.jpg");

            doReturn(CopyObjectResponse.builder().build()).when(s3Client).copyObject(any(CopyObjectRequest.class));
            doReturn(DeleteObjectResponse.builder().build()).when(s3Client).deleteObject(any(DeleteObjectRequest.class));

            List<String> result = fileClient.moveTempFilesToPermanent(domainName, domainId, tempImageKeys);

            assertThat(result).hasSize(2);
            assertThat(result.get(0)).isEqualTo("cheer/123/temp1.jpg");
            assertThat(result.get(1)).isEqualTo("cheer/123/temp2.jpg");
        }
    }
}
