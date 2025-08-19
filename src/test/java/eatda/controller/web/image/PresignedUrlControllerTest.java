package eatda.controller.web.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import eatda.controller.BaseControllerTest;
import eatda.exception.BusinessErrorCode;
import eatda.exception.ErrorResponse;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PresignedUrlControllerTest extends BaseControllerTest {

    @Nested
    class GetPresignedUrl {

        @Test
        void 프리사인드_URL을_발급받을_수_있다() {
            PresignedUrlRequest request = new PresignedUrlRequest(
                    List.of(
                            new PresignedUrlRequest.FileDetail(1, "image/png", 1024L),
                            new PresignedUrlRequest.FileDetail(2, "image/jpeg", 2048L)
                    )
            );

            PresignedUrlResponse response = given()
                    .header("Authorization", accessToken())
                    .contentType("application/json")
                    .body(request)
                    .when()
                    .post("/api/image/presigned-url")
                    .then()
                    .statusCode(200)
                    .extract().as(PresignedUrlResponse.class);

            assertAll(
                    () -> assertThat(response.urls()).isNotEmpty(),
                    () -> assertThat(response.urls()).hasSize(2),
                    () -> assertThat(response.urls().get(0).order()).isEqualTo(1),
                    () -> assertThat(response.urls().get(1).order()).isEqualTo(2)
            );
        }

        @Test
        void 잘못된_컨텐츠타입이면_예외가_발생한다() {
            PresignedUrlRequest invalidRequest = new PresignedUrlRequest(
                    List.of(
                            new PresignedUrlRequest.FileDetail(1, "text/plain", 1024L)
                    )
            );

            ErrorResponse response = given()
                    .header("Authorization", accessToken())
                    .contentType("application/json")
                    .body(invalidRequest)
                    .when()
                    .post("/api/image/presigned-url")
                    .then()
                    .statusCode(400)
                    .extract().as(ErrorResponse.class);

            assertAll(
                    () -> assertThat(response.errorCode()).isEqualTo(BusinessErrorCode.INVALID_IMAGE_TYPE.getCode()),
                    () -> assertThat(response.message()).isEqualTo(BusinessErrorCode.INVALID_IMAGE_TYPE.getMessage())
            );
        }
    }
}
