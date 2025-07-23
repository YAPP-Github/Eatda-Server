package eatda.storage.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import eatda.client.file.FileClient;
import eatda.domain.Image;
import eatda.domain.ImageDomain;
import eatda.domain.ImageKey;
import eatda.storage.BaseStorageTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class ImageStorageTest extends BaseStorageTest {

    private FileClient fileClient;
    private ExternalImageStorage externalImageStorage;
    private CachePreSignedUrlStorage cachePreSignedUrlStorage;
    private ImageStorage imageStorage;

    @BeforeEach
    void setUp() {
        fileClient = mock(FileClient.class);
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
            Image image = new Image(ImageDomain.MEMBER, file);
            ImageKey expectedImageKey = new ImageKey("test-image-key");
            doReturn(expectedImageKey).when(externalImageStorage).upload(image);

            ImageKey actualImageKey = imageStorage.upload(image);

            assertThat(actualImageKey).isEqualTo(expectedImageKey);
        }

        @Test
        void 이미지_업로드_시_PreSignedUrl이_캐시에_저장된다() {
            MockMultipartFile file = new MockMultipartFile(
                    "image", "test-image.jpg", "image/jpeg", "image-content".getBytes()
            );
            Image image = new Image(ImageDomain.MEMBER, file);
            ImageKey imageKey = new ImageKey("test-image-key");
            doReturn(imageKey).when(externalImageStorage).upload(image);
            doReturn("https://example.url.com").when(externalImageStorage).getPreSignedUrl(imageKey);

            imageStorage.upload(image);

            assertThat(cachePreSignedUrlStorage.get(new ImageKey("test-image-key"))).contains(
                    "https://example.url.com");
        }
    }

    @Nested
    class GetPreSignedUrl {

        @Test
        void 이미지_키가_null이면__null을_반환한다() {
            ImageKey imageKey = null;

            String actual = imageStorage.getPreSignedUrl(imageKey);

            assertThat(actual).isNull();
        }

        @Test
        void 이미지_키가_캐시에_존재하면_s3에_요청하지_않고_PreSignedUrl을_반환한다() {
            ImageKey imageKey = new ImageKey("test-image-key");
            cachePreSignedUrlStorage.put(imageKey, "https://example.url.com");

            String preSignedUrl = imageStorage.getPreSignedUrl(imageKey);

            assertAll(
                    () -> assertThat(preSignedUrl).isEqualTo("https://example.url.com"),
                    () -> verify(externalImageStorage, never()).getPreSignedUrl(imageKey)
            );
        }

        @Test
        void 이미지_키가_캐시에_존재하지_않으면_S3에서_PreSignedUrl을_조회하고_캐시에_저장한다() {
            ImageKey imageKey = new ImageKey("test-image-key");
            doReturn("https://example.url.com").when(externalImageStorage).getPreSignedUrl(imageKey);

            String preSignedUrl = imageStorage.getPreSignedUrl(imageKey);

            assertAll(
                    () -> assertThat(preSignedUrl).isEqualTo("https://example.url.com"),
                    () -> assertThat(cachePreSignedUrlStorage.get(imageKey)).contains("https://example.url.com")
            );
        }
    }
}
