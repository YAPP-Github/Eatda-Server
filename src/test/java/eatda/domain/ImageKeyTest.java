package eatda.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ImageKeyTest {

    @Nested
    class IsEmpty {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n\t"})
        void 이미지_키값이_비어있다(String value) {
            ImageKey imageKey = new ImageKey(value);

            boolean actual = imageKey.isEmpty();

            assertThat(actual).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "story/550e8400-e29b-41d4-a716-446655440000.jpg",
                "cheer/550e8400-e29b-41d4-a716-446655440111.png"})
        void 이미지_키값이_비어있지_않다(String value) {
            ImageKey imageKey = new ImageKey(value);

            boolean actual = imageKey.isEmpty();

            assertThat(actual).isFalse();
        }
    }
}
