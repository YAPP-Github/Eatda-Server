package eatda.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ImageKeyTest {

    @Nested
    class Validate {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n\t"})
        void 이미지_키값이_비어있다면_예외를_던진다(String value) {
            BusinessException exception = assertThrows(BusinessException.class, () -> new ImageKey(value));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_IMAGE_KEY);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "story/550e8400-e29b-41d4-a716-446655440000.jpg",
                "cheer/550e8400-e29b-41d4-a716-446655440111.png"})
        void 이미지_키값이_유효하다면_예외를_던지지_않는다(String value) {
            ImageKey imageKey = new ImageKey(value);

            assertThat(imageKey.getValue()).isEqualTo(value);
        }
    }
}
