package timeeat.client.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.URI;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OauthClientTest {

    private final String clientId = "testClientId";
    private final String redirectUri = "http://localhost:8080/oauth/callback";
    private final OauthClient oauthClient = new OauthClient(clientId, redirectUri);

    @Nested
    class GetOauthLoginUrl {

        @Test
        void Oauth_로그인_URL을_생성할_수_있다() {
            URI uri = oauthClient.getOauthLoginUrl();

            assertAll(
                    () -> assertThat(uri.getHost()).isEqualTo("kauth.kakao.com"),
                    () -> assertThat(uri.getPath()).isEqualTo("/oauth/authorize"),
                    () -> assertThat(uri.getQuery()).contains("client_id=%s".formatted(clientId)),
                    () -> assertThat(uri.getQuery()).contains("redirect_uri=%s".formatted(redirectUri)),
                    () -> assertThat(uri.getQuery()).contains("response_type=code")
            );
        }
    }
}
