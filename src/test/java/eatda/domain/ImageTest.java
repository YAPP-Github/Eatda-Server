package eatda.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class ImageTest {

    @Nested
    class Validate {

        @ValueSource(strings = {"image/jpg", "image/jpeg", "image/png"})
        @ParameterizedTest
        void 파일_타입은_이미지_타입이어야_한다(String contentType) {
            ImageDomain domain = ImageDomain.STORY;
            MultipartFile file = new MockMultipartFile("file", "test.jpg", contentType, new byte[0]);

            assertThatCode(() -> new Image(domain, file))
                    .doesNotThrowAnyException();
        }

        @ValueSource(strings = {"application/pdf", "text/plain", "image/gif"})
        @ParameterizedTest
        void 파일_타입은_이미지가_아닐_경우_예외를_던진다(String contentType) {
            ImageDomain domain = ImageDomain.STORY;
            MultipartFile file = new MockMultipartFile("file", "test.pdf", contentType, new byte[0]);

            BusinessException exception = assertThrows(BusinessException.class, () -> new Image(domain, file));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_IMAGE_TYPE);
        }
    }

    @Nested
    class GetExtension {

        @CsvSource({"test.jpg, jpg", "iamIronMan.jpeg, jpeg", "legend1_developer123.png, png"})
        @ParameterizedTest
        void 파일_이름에_확장자가_존재하는_경우_해당_확장자를_반환한다(String filename, String expected) {
            ImageDomain domain = ImageDomain.STORY;
            MultipartFile file = new MockMultipartFile("file", filename, "image/jpeg", new byte[0]);
            Image image = new Image(domain, file);

            String actual = image.getExtension();

            assertThat(actual).isEqualTo(expected);
        }

        @ValueSource(strings = {"test", ".jpg", "test."})
        @ParameterizedTest
        void 파일_이름에_확장자가_존재하지_않는_경우_기본값을_반환한다(String filename) {
            ImageDomain domain = ImageDomain.STORY;
            MultipartFile file = new MockMultipartFile("file", filename, "image/jpeg", new byte[0]);
            Image image = new Image(domain, file);
            String expected = "bin";

            String actual = image.getExtension();

            assertThat(actual).isEqualTo(expected);
        }
    }
}
