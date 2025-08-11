package eatda.client.map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class KakaoPropertiesTest {

    @Nested
    class ValidateApiKey {

        @ValueSource(strings = {"\n", "    "})
        @ParameterizedTest
        @NullAndEmptySource
        void null_또는_빈_문자열인_API_키는_예외를_던진다(String apiKey) {
            assertThatThrownBy(() -> new KakaoProperties(apiKey))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("API key must not be null or blank");

        }

        @Test
        void 유효한_API_키는_정상적으로_생성된다() {
            assertThatCode(() -> new KakaoProperties("z116bf75dgh76c253hg7c4b123ab3609"))
                    .doesNotThrowAnyException();
        }
    }

}
