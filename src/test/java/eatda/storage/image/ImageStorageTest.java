package eatda.storage.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import eatda.domain.ImageDomain;
import eatda.storage.BaseStorageTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.mock.web.MockMultipartFile;

class ImageStorageTest extends BaseStorageTest {

    private ExternalImageStorage externalImageStorage;
    private CachePreSignedUrlStorage cachePreSignedUrlStorage;
    private ImageStorage imageStorage;

    @BeforeEach
    void setUp() {
        externalImageStorage = mock(ExternalImageStorage.class);
        cachePreSignedUrlStorage = new CachePreSignedUrlStorage(getCacheManager());
        imageStorage = new ImageStorage(externalImageStorage, cachePreSignedUrlStorage);
    }

    @Nested
    class Upload {

        @Test
        void 이미지가_S3에_업로드된다() {
            MockMultipartFile file = new MockMultipartFile(
                    "image", "test-image.jpg", "image/jpeg", "image-content".getBytes()
            );
            doReturn("test-image-key").when(externalImageStorage).upload(file, ImageDomain.MEMBER);

            String imageKey = imageStorage.upload(file, ImageDomain.MEMBER);

            assertThat(imageKey).isEqualTo("test-image-key");
        }

        @Test
        void 이미지_업로드_시_PreSignedUrl이_캐시에_저장된다() {
            MockMultipartFile file = new MockMultipartFile(
                    "image", "test-image.jpg", "image/jpeg", "image-content".getBytes()
            );
            doReturn("test-image-key").when(externalImageStorage).upload(file, ImageDomain.MEMBER);
            doReturn("https://example.url.com").when(externalImageStorage).getPresignedUrl("test-image-key");

            imageStorage.upload(file, ImageDomain.MEMBER);

            assertThat(cachePreSignedUrlStorage.get("test-image-key")).contains("https://example.url.com");
        }
    }

    @Nested
    class GetPresignedUrl {

        @ParameterizedTest
        @NullAndEmptySource
        void 이미지_키가_null이면__null을_반환한다(String imageKey) {
            String actual = imageStorage.getPresignedUrl(imageKey);

            assertThat(actual).isNull();
        }

        @Test
        void 이미지_키가_캐시에_존재하면_s3에_요청하지_않고_PreSignedUrl을_반환한다() {
            String imageKey = "test-image-key";
            cachePreSignedUrlStorage.put(imageKey, "https://example.url.com");

            String preSignedUrl = imageStorage.getPresignedUrl(imageKey);

            assertAll(
                    () -> assertThat(preSignedUrl).isEqualTo("https://example.url.com"),
                    () -> verify(externalImageStorage, never()).getPresignedUrl(anyString())
            );
        }

        @Test
        void 이미지_키가_캐시에_존재하지_않으면_S3에서_PreSignedUrl을_조회하고_캐시에_저장한다() {
            String imageKey = "test-image-key";
            doReturn("https://example.url.com").when(externalImageStorage).getPresignedUrl(imageKey);

            String preSignedUrl = imageStorage.getPresignedUrl(imageKey);

            assertAll(
                    () -> assertThat(preSignedUrl).isEqualTo("https://example.url.com"),
                    () -> assertThat(cachePreSignedUrlStorage.get(imageKey)).contains("https://example.url.com")
            );
        }
    }
}
