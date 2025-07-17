package eatda.document.store;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

import eatda.controller.store.CheerPreviewResponse;
import eatda.controller.store.CheersResponse;
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

public class CheerDocumentTest extends BaseDocumentTest {

    @Nested
    class Get {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORE_API)
                .summary("최신 응원 검색")
                .queryParameter(
                        parameterWithName("size").description("조회 개수")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("cheers").type(ARRAY).description("응원 검색 결과"),
                        fieldWithPath("cheers[].storeId").type(NUMBER).description("가게 ID"),
                        fieldWithPath("cheers[].imageUrl").type(STRING).description("이미지 URL").optional(),
                        fieldWithPath("cheers[].storeName").type(STRING).description("가게 이름"),
                        fieldWithPath("cheers[].storeDistrict").type(STRING).description("가게 주소 (구)"),
                        fieldWithPath("cheers[].storeNeighborhood").type(STRING).description("가게 주소 (동)"),
                        fieldWithPath("cheers[].storeCategory").type(STRING).description("가게 카테고리"),
                        fieldWithPath("cheers[].cheerId").type(NUMBER).description("응원 ID"),
                        fieldWithPath("cheers[].cheerDescription").type(STRING).description("응원 내용")
                );

        @Test
        void 음식점_검색_성공() {
            int size = 2;
            CheersResponse responses = new CheersResponse(List.of(
                    new CheerPreviewResponse(2L, "https://example.image", "농민백암순대 본점", "강남구", "선릉구", "한식", 2L,
                            "너무 맛있어요!"),
                    new CheerPreviewResponse(1L, null, "석관동떡볶이", "성북구", "석관동", "기타", 1L,
                            "너무 매워요! 하지만 맛있어요!")
            ));
            doReturn(responses).when(cheerService).getCheers(anyInt());

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
            int size = 2;
            doThrow(new BusinessException(errorCode)).when(cheerService).getCheers(anyInt());

            var document = document("cheer/get-many", 200)
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
}
