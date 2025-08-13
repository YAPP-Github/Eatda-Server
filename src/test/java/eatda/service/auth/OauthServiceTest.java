package eatda.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import eatda.client.oauth.OauthMemberInformation;
import eatda.client.oauth.OauthToken;
import eatda.service.BaseServiceTest;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OauthServiceTest extends BaseServiceTest {

    private static final OauthToken DEFAULT_OAUTH_TOKEN = new OauthToken("oauth-access-token");
    private static final OauthMemberInformation DEFAULT_OAUTH_MEMBER_INFO =
            new OauthMemberInformation(123L, "authService@kakao.com", "nickname");

    @BeforeEach
    protected final void mockingClient() throws URISyntaxException {
        doReturn(new URI("http://localhost:8080/login/callback")).when(oauthClient).getOauthLoginUrl(anyString());
        doReturn(DEFAULT_OAUTH_TOKEN).when(oauthClient).requestOauthToken(anyString(), anyString());
        doReturn(DEFAULT_OAUTH_MEMBER_INFO).when(oauthClient).requestMemberInformation(DEFAULT_OAUTH_TOKEN);
    }

    @Nested
    class GetOauthLoginUrl {

        @Test
        void OAuth_로그인_URL을_반환한다() {
            String origin = "http://localhost:3000";

            URI oauthLoginUrl = oauthService.getOauthLoginUrl(origin);

            assertThat(oauthLoginUrl).isNotNull();
        }
    }

    @Nested
    class RequestOauthToken {

        @Test
        void OAuth_토큰을_반환한다() {
            String code = "oauth-code";
            String url = "http://localhost:3000";

            OauthMemberInformation actual = oauthService.getOAuthInformation(code, url);

            assertThat(actual).isEqualTo(DEFAULT_OAUTH_MEMBER_INFO);
        }
    }
}
