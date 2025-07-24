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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

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
    class Upload {

        @Test
        void 주어진_속성을_바탕으로_파일을_업로드한다() {
            MultipartFile file = new MockMultipartFile("file", "test-file.jpg", "image/jpeg", new byte[0]);
            String fileKey = "test-file-key.jpg";
            doReturn(PutObjectResponse.builder().build())
                    .when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

            String actual = fileClient.upload(file, fileKey);

            assertThat(actual).isEqualTo(fileKey);
        }

        @Test
        void 업로드에_문제가_생길_경우_서비스_에러_처리를_한다() {
            MultipartFile file = new MockMultipartFile("file", "test-file.jpg", "image/jpeg", new byte[0]);
            String fileKey = "test-file-key.jpg";
            doThrow(SdkClientException.create("Upload failed"))
                    .when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

            BusinessException exception = assertThrows(BusinessException.class, () -> fileClient.upload(file, fileKey));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Nested
    class GetPreSignedUrl {

        @Test
        void 주어진_파일_Key에_대해_사전_서명된_URL을_반환한다() throws MalformedURLException {
            String fileKey = "test-file-key.jpg";
            String expected = "https://example.com/test-file-key.jpg";
            doReturn(mockRequest(expected)).when(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));

            String actual = fileClient.getPreSignedUrl(fileKey, Duration.ofMinutes(10));

            assertThat(actual).isEqualTo(expected);
        }

        private PresignedGetObjectRequest mockRequest(String url) throws MalformedURLException {
            PresignedGetObjectRequest request = mock(PresignedGetObjectRequest.class);
            doReturn(new URL(url)).when(request).url();
            return request;
        }

        @Test
        void 문제가_생길_경우_서비스_에러_처리를_한다() {
            String fileKey = "test-file-key.jpg";
            doThrow(SdkClientException.create("Presigned URL generation failed"))
                    .when(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> fileClient.getPreSignedUrl(fileKey, Duration.ofMinutes(10)));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.PRESIGNED_URL_GENERATION_FAILED);
        }
    }
}
