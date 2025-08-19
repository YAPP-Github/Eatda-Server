package eatda.document.web.image;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import eatda.controller.web.image.PresignedUrlInfo;
import eatda.controller.web.image.PresignedUrlRequest;
import eatda.controller.web.image.PresignedUrlResponse;
import eatda.document.BaseDocumentTest;
import eatda.document.RestDocsRequest;
import eatda.document.RestDocsResponse;
import eatda.document.Tag;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpHeaders;

public class PresignedUrlDocumentControllerTest extends BaseDocumentTest {

    @Nested
    class GetPresignedUrl {

        RestDocsRequest requestDocument = request()
                .tag(Tag.IMAGE_API)
                .summary("프리사인드 URL 발급")
                .description("클라이언트가 다중 파일 업로드를 위해 S3 프리사인드 URL을 요청하는 API")
                .requestHeader(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ).requestBodyField(
                        fieldWithPath("fileDetails").type(ARRAY).description("업로드할 파일 상세 목록"),
                        fieldWithPath("fileDetails[].order").type(NUMBER).description("파일 순서 (오름차순 정렬 추천)"),
                        fieldWithPath("fileDetails[].contentType").type(STRING).description("파일 MIME 타입"),
                        fieldWithPath("fileDetails[].fileSize").type(NUMBER).description("파일 크기(Byte)")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("urls").type(ARRAY).description("발급된 프리사인드 URL 목록"),
                        fieldWithPath("urls[].order").type(NUMBER).description("파일 순서"),
                        fieldWithPath("urls[].contentType").type(STRING).description("파일 MIME 타입"),
                        fieldWithPath("urls[].key").type(STRING).description("S3 오브젝트 키"),
                        fieldWithPath("urls[].url").type(STRING).description("S3 업로드용 프리사인드 URL"),
                        fieldWithPath("urls[].expiresIn").type(NUMBER).description("URL 만료까지 남은 시간(초 단위)")
                );

        @Test
        void 프리사인드_URL_발급_성공() {
            PresignedUrlResponse response = new PresignedUrlResponse(
                    List.of(
                            new PresignedUrlInfo(1, "image/png", "temp/HASH.png",
                                    "https://cdn.example.com/story/123/image1.png", 3600L),
                            new PresignedUrlInfo(2, "image/jpeg", "temp/HASH.jpg",
                                    "https://cdn.example.com/story/123/image2.jpg", 3600L)
                    )
            );

            doReturn(response).when(presignedUrlService).generatePresignedUrl(any(PresignedUrlRequest.class));

            var document = document("image/presigned-url", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            PresignedUrlRequest request = new PresignedUrlRequest(
                    List.of(
                            new PresignedUrlRequest.FileDetail(1, "image/png", 1024L),
                            new PresignedUrlRequest.FileDetail(2, "image/jpeg", 2048L)
                    )
            );

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .body(request)
                    .when().post("/api/image/presigned-url")
                    .then().statusCode(200);
        }

        @EnumSource(value = BusinessErrorCode.class,
                names = {"INVALID_IMAGE_TYPE", "UNAUTHORIZED_MEMBER", "EXPIRED_TOKEN"})
        @ParameterizedTest
        void 프리사인드_URL_발급_실패(BusinessErrorCode errorCode) {
            doThrow(new BusinessException(errorCode))
                    .when(presignedUrlService).generatePresignedUrl(any(PresignedUrlRequest.class));

            var document = document("image/presigned-url", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            PresignedUrlRequest request = new PresignedUrlRequest(
                    List.of(new PresignedUrlRequest.FileDetail(1, "text/plain", 1024L))
            );

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .body(request)
                    .when().post("/api/image/presigned-url")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }
}
