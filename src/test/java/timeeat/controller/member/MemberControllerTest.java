package timeeat.controller.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.controller.BaseControllerTest;

class MemberControllerTest extends BaseControllerTest {

    @Nested
    class ReissueToken {

        @Test
        void 리프레시_토큰을_이용해_토큰을_재발급한다() {
            ReissueRequest request = new ReissueRequest(refreshToken());

            TokenResponse response = given().body(request)
                    .contentType(ContentType.JSON)
                    .when().post("/api/member/reissue")
                    .then()
                    .statusCode(200)
                    .extract().as(TokenResponse.class);

            assertAll(
                    () -> assertThat(response.accessToken()).isNotBlank(),
                    () -> assertThat(response.refreshToken()).isNotBlank()
            );
        }
    }
}
