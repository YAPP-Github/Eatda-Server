package eatda.document.store;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

import eatda.controller.store.StoreSearchResponse;
import eatda.controller.store.StoreSearchResponses;
import eatda.document.BaseDocumentTest;
import eatda.document.RestDocsRequest;
import eatda.document.RestDocsResponse;
import eatda.document.Tag;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class StoreDocumentTest extends BaseDocumentTest {

    @Nested
    class SearchStores {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORE_API)
                .summary("음식점 검색")
                .requestHeader(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ).queryParameter(
                        parameterWithName("query").description("검색어")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("stores").type(ARRAY).description("음식점 검색 결과"),
                        fieldWithPath("stores[].kakaoId").type(STRING).description("카카오 음식점 ID"),
                        fieldWithPath("stores[].name").type(STRING).description("음식점 이름"),
                        fieldWithPath("stores[].address").type(STRING).description("음식점 주소 (지번 주소)")
                );

        @Test
        void 음식점_검색_결과를_반환한다() {
            String query = "농민백암순대";
            StoreSearchResponses responses = new StoreSearchResponses(List.of(
                    new StoreSearchResponse("17163273", "농민백암순대 본점", "서울 강남구 대치동 896-33"),
                    new StoreSearchResponse("1062153333", "농민백암순대 시청직영점", "서울 중구 북창동 19-4")
            ));
            doReturn(responses).when(storeService).searchStores(anyString());

            var document = document("store/search", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("query", query)
                    .when().get("/api/shop/search")
                    .then().statusCode(200);
        }
    }
}
