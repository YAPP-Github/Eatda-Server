package timeeat.document.member;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.document.BaseDocumentTest;

public class MemberDocumentTest extends BaseDocumentTest {

    @Nested
    class RedirectOauthLoginPage {

        @Test
        void Oauth_로그인_페이지로_리다이렉트_할_수_있다() {
            var document = document("member/oauth_redirect", 302).build();

            given(document)
                    .redirects().follow(false)
                    .when()
                    .get("/api/login/auth")
                    .then()
                    .statusCode(302);
        }
    }
}
