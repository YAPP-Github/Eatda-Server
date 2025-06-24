package timeeat.document.member;

import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.controller.member.ReissueRequest;
import timeeat.document.BaseDocumentTest;
import timeeat.document.RestDocsRequest;
import timeeat.document.RestDocsResponse;
import timeeat.document.Tag;


public class MemberDocumentTest extends BaseDocumentTest {

    @Nested
    class ReissueToken {

        private final RestDocsRequest requestDocument = request()
                .tag(Tag.MEMBER_API)
                .summary("토큰 재발급")
                .requestBodyField(
                        fieldWithPath("refreshToken").type(STRING).description("리프레시 토큰")
                );

        private final RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("accessToken").type(STRING).description("액세스 토큰"),
                        fieldWithPath("refreshToken").type(STRING).description("리프레시 토큰")
                );

        @Test
        void 토큰_재발급_성공() {
            ReissueRequest request = new ReissueRequest(refreshToken());

            var document = document("member/reissue", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/api/member/reissue")
                    .then().statusCode(200);
        }

    }
}
