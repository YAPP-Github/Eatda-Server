package eatda.controller.store;

import static org.assertj.core.api.Assertions.assertThat;

import eatda.controller.BaseControllerTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class StoreControllerTest extends BaseControllerTest {

    @Nested
    class SearchStores {

        @Test
        void 음식점_검색_결과를_반환한다() {
            String query = "농민백암순대";

            StoreSearchResponses responses = given()
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .queryParam("query", query)
                    .when()
                    .get("/api/shop/search")
                    .then()
                    .statusCode(200)
                    .extract().as(StoreSearchResponses.class);

            assertThat(responses.stores()).isNotEmpty();
        }
    }

}
