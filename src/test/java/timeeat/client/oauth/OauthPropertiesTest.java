package timeeat.client.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import timeeat.exception.InitializeException;

class OauthPropertiesTest {

    @Nested
    class Validate {

        @ParameterizedTest
        @NullAndEmptySource
        void 클라이언트_아이디가_비어있는_경우_예외를_던진다(String clientId) {
            assertThatThrownBy(() -> new OauthProperties(clientId, "/path", List.of("http://localhost:8080")))
                    .isInstanceOf(InitializeException.class)
                    .hasMessage("Client ID must not be null or empty");
        }

        @ParameterizedTest
        @ValueSource(strings = {"path", ".path", "path/", ""})
        void 리다이렉트_경로가_경로_형식이_아닌_경우_예외를_던진다(String redirectPath) {
            assertThatThrownBy(() -> new OauthProperties("client-id", redirectPath, List.of("http://localhost:8080")))
                    .isInstanceOf(InitializeException.class)
                    .hasMessage("Redirect path must not be null or start with '/'");
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalid-url", "http://", "http://:8080", "http://localhost:8080/path", " "})
        void 허용된_오리진이_유효하지_않은_URL인_경우_예외를_던진다(String origin) {
            assertThatThrownBy(() -> new OauthProperties("client-id", "/path", List.of(origin)))
                    .isInstanceOf(InitializeException.class)
                    .hasMessageContaining("Allowed origin must be a valid origin form");
        }
    }

    @Nested
    class IsAllowedOrigin {

        @ParameterizedTest
        @ValueSource(strings = {"http://localhost:8080", " http://localhost:8080 ",
                "https://example.com", "https://example.com/"})
        void 허용된_오리진인_경우_true를_반환한다(String allowedOrigin) {
            List<String> origins = List.of("http://localhost:8080", "https://example.com");
            OauthProperties oauthProperties = new OauthProperties("client-id", "/path", origins);

            boolean isAllowed = oauthProperties.isAllowedOrigin(allowedOrigin);

            assertThat(isAllowed).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"https://not-allowed.com", "http://localhost:8080/path", "http://localhost:8080nono"})
        void 허용되지_않은_오리진인_경우_false를_반환한다() {
            OauthProperties oauthProperties = new OauthProperties("client-id", "/path",
                    List.of("http://localhost:8080"));

            boolean isAllowed = oauthProperties.isAllowedOrigin("https://not-allowed.com");

            assertThat(isAllowed).isFalse();
        }
    }
}
