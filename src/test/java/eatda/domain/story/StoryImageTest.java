package eatda.domain.story;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class StoryImageTest {

    @Nested
    class Validate {

        @Test
        void 스토리_이미지가_정상적으로_생성된다() {
            StoryImage image = new StoryImage(null, "story/1.png", 0, "image/png", 12345L);

            assertThat(image.getImageKey()).isEqualTo("story/1.png");
            assertThat(image.getOrderIndex()).isEqualTo(0);
            assertThat(image.getContentType()).isEqualTo("image/png");
            assertThat(image.getFileSize()).isEqualTo(12345L);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 이미지키가_비어있으면_예외가_발생한다(String imageKey) {
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> new StoryImage(null, imageKey, 0, "image/png", 12345L));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_EMPTY_FILE_DETAILS);
        }

        @Test
        void 파일사이즈가_null이면_예외가_발생한다() {
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> new StoryImage(null, "story/1.png", 0, "image/png", null));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_MAX_FILE_SIZE);
        }

        @Test
        void 파일사이즈가_0이하면_예외가_발생한다() {
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> new StoryImage(null, "story/1.png", 0, "image/png", 0L));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_MAX_FILE_SIZE);
        }

        @Test
        void contentType이_null이면_예외가_발생한다() {
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> new StoryImage(null, "story/1.png", 0, null, 12345L));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_IMAGE_TYPE);
        }
    }
}
