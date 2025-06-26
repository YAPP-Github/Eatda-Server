package timeeat.document.member;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import timeeat.document.BaseDocumentTest;
import timeeat.document.RestDocsRequest;
import timeeat.document.RestDocsResponse;
import timeeat.document.Tag;

public class MemberDocumentTest extends BaseDocumentTest {

    @Nested
    class RedirectOauthLoginPage {

        RestDocsRequest requestDocument = request()
                .tag(Tag.MEMBER_API)
                .summary("OAuth 로그인 페이지 리다이렉트");

        RestDocsResponse responseDocument = response()
                .responseHeader(
                        headerWithName(HttpHeaders.LOCATION).description("리다이렉트 URI")
                );

        @Test
        void Oauth_로그인_페이지로_리다이렉트_할_수_있다() {
            var document = document("member/oauth_redirect", 302)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .redirects().follow(false)
                    .when()
                    .get("/api/member/login/auth")
                    .then()
                    .statusCode(302);
        }
    }
}
