package eatda.document.store;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

import eatda.controller.cheer.CheerImageResponse;
import eatda.controller.cheer.CheerInStoreResponse;
import eatda.controller.cheer.CheerPreviewResponse;
import eatda.controller.cheer.CheerRegisterRequest;
import eatda.controller.cheer.CheerResponse;
import eatda.controller.cheer.CheersInStoreResponse;
import eatda.controller.cheer.CheersResponse;
import eatda.document.BaseDocumentTest;
import eatda.document.RestDocsRequest;
import eatda.document.RestDocsResponse;
import eatda.document.Tag;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import io.restassured.http.ContentType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpHeaders;

public class CheerDocumentTest extends BaseDocumentTest {

    @Nested
    class RegisterCheer {

        private static final String REQUEST_DESCRIPTION_MARKDOWN = """
                - 요청 형식 : application/json
                - 요청 body 예시
                    ```json
                    {
                      "storeName": "깜브레",
                      "storeKakaoId": "2085990843",
                      "description": "정말 맛있어요! 강추합니다!",
                      "images": [
                        {
                          "imageKey": "temp/1.png",
                          "orderIndex": 0,
                          "contentType": "image/png",
                          "fileSize": 12345
                        },
                        {
                          "imageKey": "temp/2.png",
                          "orderIndex": 1,
                          "contentType": "image/png",
                          "fileSize": 67890
                        }
                      ]
                    }
                    ```
                """;

        RestDocsRequest requestDocument = request()
                .tag(Tag.CHEER_API)
                .summary("응원 등록")
                .description(REQUEST_DESCRIPTION_MARKDOWN)
                .requestHeader(
                        headerWithName("Authorization").description("인증 토큰")
                )
                .requestBodyField(
                        fieldWithPath("storeKakaoId").type(STRING).description("가게 카카오 ID"),
                        fieldWithPath("storeName").type(STRING).description("가게 이름"),
                        fieldWithPath("description").type(STRING).description("응원 내용"),
                        fieldWithPath("images").type(ARRAY).description("응원 이미지 리스트").optional(),
                        fieldWithPath("images[].imageKey").type(STRING).description("이미지 key"),
                        fieldWithPath("images[].orderIndex").type(NUMBER).description("이미지 순서 인덱스"),
                        fieldWithPath("images[].contentType").type(STRING).description("이미지 MIME 타입"),
                        fieldWithPath("images[].fileSize").type(NUMBER).description("이미지 파일 크기 (byte 단위)")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("storeId").type(NUMBER).description("가게 ID"),
                        fieldWithPath("cheerId").type(NUMBER).description("응원 ID"),
                        fieldWithPath("images").type(ARRAY).description("응원 이미지 리스트").optional(),
                        fieldWithPath("images[].imageKey").type(STRING).description("이미지 key").optional(),
                        fieldWithPath("images[].orderIndex").type(NUMBER).description("이미지 순서 인덱스").optional(),
                        fieldWithPath("images[].contentType").type(STRING).description("이미지 MIME 타입").optional(),
                        fieldWithPath("images[].fileSize").type(NUMBER).description("이미지 파일 크기 (byte 단위)").optional(),
                        fieldWithPath("images[].url").type(STRING).description("이미지 접근 URL").optional(),
                        fieldWithPath("cheerDescription").type(STRING).description("응원 내용")
                );


        @Test
        void 응원_등록_성공() {
            CheerRegisterRequest request = new CheerRegisterRequest(
                    "깜브레",
                    "2085990843",
                    "정말 맛있어요! 강추합니다!",
                    List.of(
                            new CheerRegisterRequest.UploadedImageDetail("temp/1.png", 0, "image/png", 12345),
                            new CheerRegisterRequest.UploadedImageDetail("temp/2.png", 1, "image/png", 67890)
                    )
            );

            CheerResponse response = new CheerResponse(
                    1L,
                    1L,
                    List.of(
                            new CheerImageResponse("temp/1.png", 0, "image/png", 12345L, "https://.../1.png"),
                            new CheerImageResponse("temp/2.png", 1, "image/png", 67890L, "https://.../2.png")
                    ),
                    "정말 맛있어요! 강추합니다!"
            );

            doReturn(response).when(cheerService).registerCheer(eq(request), any(), anyLong(), any());

            var document = document("cheer/register", 201)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/api/cheer")
                    .then().statusCode(201);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {
                "UNAUTHORIZED_MEMBER",
                "EXPIRED_TOKEN",
                "FULL_CHEER_SIZE_PER_MEMBER",
                "MAP_SERVER_ERROR",
                "STORE_NOT_FOUND",
                "FILE_UPLOAD_FAILED",
                "INVALID_IMAGE_TYPE",
                "PRESIGNED_URL_GENERATION_FAILED",
                "INVALID_CHEER_DESCRIPTION"})
        @ParameterizedTest
        void 응원_등록_실패(BusinessErrorCode errorCode) {
            CheerRegisterRequest request = new CheerRegisterRequest(
                    "농민백암순대 본점",
                    "123",
                    "너무 맛있어요!",
                    List.of(
                            new CheerRegisterRequest.UploadedImageDetail("temp/1.png", 0, "image/png", 12345)
                    )
            );

            doThrow(new BusinessException(errorCode))
                    .when(cheerService).registerCheer(eq(request), any(), anyLong(), any());

            var document = document("cheer/register", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/api/cheer")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }

    @Nested
    class GetCheers {

        RestDocsRequest requestDocument = request()
                .tag(Tag.CHEER_API)
                .summary("최신 응원 검색")
                .queryParameter(
                        parameterWithName("page").description("조회 페이지 (기본값 0, 최소 0)").optional(),
                        parameterWithName("size").description("조회 개수 (기본값 5, 최소 1, 최대 50)").optional()
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("cheers").type(ARRAY).description("응원 검색 결과"),
                        fieldWithPath("cheers[].storeId").type(NUMBER).description("가게 ID"),
                        fieldWithPath("cheers[].images").type(ARRAY).description("응원 이미지 리스트").optional(),
                        fieldWithPath("cheers[].images[].imageKey").type(STRING).description("이미지 key").optional(),
                        fieldWithPath("cheers[].images[].orderIndex").type(NUMBER).description("이미지 순서 인덱스").optional(),
                        fieldWithPath("cheers[].images[].contentType").type(STRING).description("이미지 MIME 타입").optional(),
                        fieldWithPath("cheers[].images[].fileSize").type(NUMBER).description("이미지 파일 크기 (byte 단위)").optional(),
                        fieldWithPath("cheers[].images[].url").type(STRING).description("이미지 접근 URL").optional(),
                        fieldWithPath("cheers[].storeName").type(STRING).description("가게 이름"),
                        fieldWithPath("cheers[].storeDistrict").type(STRING).description("가게 주소 (구)"),
                        fieldWithPath("cheers[].storeNeighborhood").type(STRING).description("가게 주소 (동)"),
                        fieldWithPath("cheers[].storeCategory").type(STRING).description("가게 카테고리"),
                        fieldWithPath("cheers[].cheerId").type(NUMBER).description("응원 ID"),
                        fieldWithPath("cheers[].cheerDescription").type(STRING).description("응원 내용")
                );


        @Test
        void 음식점_검색_성공() {
            int page = 0;
            int size = 2;
            CheersResponse responses = new CheersResponse(List.of(
                    new CheerPreviewResponse(2L, new ArrayList<>(), "농민백암순대 본점", "강남구", "선릉구", "한식", 2L,
                            "너무 맛있어요!"),
                    new CheerPreviewResponse(1L, new ArrayList<>(), "석관동떡볶이", "성북구", "석관동", "기타", 1L,
                            "너무 매워요! 하지만 맛있어요!")
            ));
            doReturn(responses).when(cheerService).getCheers(page, size);

            var document = document("cheer/get-many", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .queryParam("size", size)
                    .when().get("/api/cheer")
                    .then().statusCode(200);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {"PRESIGNED_URL_GENERATION_FAILED"})
        @ParameterizedTest
        void 음식점_검색_실패(BusinessErrorCode errorCode) {
            int page = 0;
            int size = 2;
            doThrow(new BusinessException(errorCode)).when(cheerService).getCheers(page, size);

            var document = document("cheer/get-many", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .queryParam("size", size)
                    .when().get("/api/cheer")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }

    @Nested
    class GetCheersByStoreId {

        RestDocsRequest requestDocument = request()
                .tag(Tag.CHEER_API)
                .summary("가게별 응원 검색")
                .pathParameter(
                        parameterWithName("storeId").description("가게 ID")
                )
                .queryParameter(
                        parameterWithName("page").description("조회 페이지 (기본값 0, 최소 0)").optional(),
                        parameterWithName("size").description("조회 개수 (기본값 5, 최소 1, 최대 50)").optional()
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("cheers").type(ARRAY).description("응원 검색 결과"),
                        fieldWithPath("cheers[].id").type(NUMBER).description("응원 ID"),
                        fieldWithPath("cheers[].memberId").type(NUMBER).description("응원 작성자 회원 ID"),
                        fieldWithPath("cheers[].memberNickname").type(STRING).description("응원 작성자 닉네임"),
                        fieldWithPath("cheers[].description").type(STRING).description("응원 내용")
                );

        @Test
        void 가게별_응원_검색_성공() {
            Long storeId = 1L;
            int page = 0;
            int size = 2;
            CheersInStoreResponse responses = new CheersInStoreResponse(List.of(
                    new CheerInStoreResponse(20L, 5L, "커찬", "너무 맛있어요!"),
                    new CheerInStoreResponse(10L, 3L, "찬커", "너무 매워요! 하지만 맛있어요!")
            ));
            doReturn(responses).when(cheerService).getCheersByStoreId(storeId, page, size);

            var document = document("cheer/get-store-id", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .when().get("/api/shops/{storeId}/cheers", storeId)
                    .then().statusCode(200);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {"STORE_NOT_FOUND"})
        @ParameterizedTest
        void 가게별_응원_검색_실패(BusinessErrorCode errorCode) {
            Long storeId = 1L;
            int page = 0;
            int size = 2;
            doThrow(new BusinessException(errorCode)).when(cheerService).getCheersByStoreId(storeId, page, size);

            var document = document("cheer/get-store-id", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .when().get("/api/shops/{storeId}/cheers", storeId)
                    .then().statusCode(errorCode.getStatus().value());
        }
    }
}
