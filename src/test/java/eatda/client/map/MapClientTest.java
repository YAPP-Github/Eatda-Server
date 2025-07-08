package eatda.client.map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;

@RestClientTest(MapClient.class)
class MapClientTest {

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private MapClient mapClient;

    private void setMockServer(HttpMethod method, String uri, String responseBody) {
        mockServer.expect(requestTo(uri))
                .andExpect(method(method))
                .andRespond(MockRestResponseCreators.withSuccess(responseBody, MediaType.APPLICATION_JSON));
    }

    @Nested
    class SearchShops {

        @Test
        void 가게_검색을_할_수_있다() {
            String query = "농민백암순대";
            String uri = "https://dapi.kakao.com/v2/local/search/keyword.json?query=%s&category=%s&page=1&size=15&sort=accuracy"
                    .formatted(URLEncoder.encode(query, StandardCharsets.UTF_8), "FD6");
            String responseBody = """
                    {
                        "documents": [
                            {
                                "address_name": "서울 강남구 대치동 896-33",
                                "category_group_code": "FD6",
                                "category_group_name": "음식점",
                                "category_name": "음식점 > 한식 > 국밥",
                                "distance": "",
                                "id": "17163273",
                                "phone": "02-555-9603",
                                "place_name": "농민백암순대 본점",
                                "place_url": "http://place.map.kakao.com/17163273",
                                "road_address_name": "서울 강남구 선릉로86길 40-4",
                                "x": "127.05300772497776",
                                "y": "37.503708148482524"
                            }
                        ],
                        "meta": {
                            "is_end": true,
                            "pageable_count": 1,
                            "same_name": {
                                "keyword": "농민백암순대",
                                "region": [],
                                "selected_region": ""
                            },
                            "total_count": 1
                        }
                    }""";
            setMockServer(HttpMethod.GET, uri, responseBody);

            ShopSearchResults results = mapClient.searchShops("농민백암순대");

            ShopSearchResult result = results.results().getFirst();
            assertAll(
                    () -> assertThat(result.kakaoId()).isEqualTo("17163273"),
                    () -> assertThat(result.categoryGroupCode()).isEqualTo("FD6"),
                    () -> assertThat(result.categoryName()).isEqualTo("음식점 > 한식 > 국밥"),
                    () -> assertThat(result.phoneNumber()).isEqualTo("02-555-9603"),
                    () -> assertThat(result.placeName()).isEqualTo("농민백암순대 본점"),
                    () -> assertThat(result.placeUrl()).isEqualTo("http://place.map.kakao.com/17163273"),
                    () -> assertThat(result.addressName()).isEqualTo("서울 강남구 대치동 896-33"),
                    () -> assertThat(result.roadAddressName()).isEqualTo("서울 강남구 선릉로86길 40-4"),
                    () -> assertThat(result.latitude()).isEqualTo(37.503708148482524),
                    () -> assertThat(result.longitude()).isEqualTo(127.05300772497776)
            );
        }
    }
}
