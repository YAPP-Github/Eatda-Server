package eatda.storage.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import eatda.client.file.FileClient;
import eatda.domain.Image;
import eatda.domain.ImageDomain;
import eatda.domain.ImageKey;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class ExternalImageStorageTest {

    private FileClient fileClient;
    private ExternalImageStorage externalImageStorage;

    @BeforeEach
    void setUp() {
        fileClient = mock(FileClient.class);
        externalImageStorage = new ExternalImageStorage(fileClient);
    }

    @Nested
    class Upload {

        @Test
        void 형식에_맞는_이미지_키가_생성된다() {
            MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[0]);
            ImageDomain domain = ImageDomain.STORY;
            Image image = new Image(domain, file);
            doReturn("uploaded-key").when(fileClient).upload(eq(file), any());

            ImageKey imageKey = externalImageStorage.upload(image);

            assertThat(imageKey.getValue()).matches("story/[0-9a-f-]{36}\\.jpg");
        }
    }

    @Nested
    class GeneratePresignedUrl {

        @Test
        void 특정_기간에_해당하는_Presigned_URL이_생성된다() {
            ImageKey key = new ImageKey("stores/image.jpg");
            Duration preSignedUrlduration = Duration.ofMinutes(30);
            String expectedUrlString = "https://example.com/presigned-url-for-image.jpg";
            doReturn(expectedUrlString).when(fileClient).getPreSignedUrl(key.getValue(), preSignedUrlduration);

            String actual = externalImageStorage.getPreSignedUrl(key);

            assertThat(actual).isEqualTo(expectedUrlString);
        }
    }
}
