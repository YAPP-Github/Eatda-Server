package eatda.client.map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import java.util.List;
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

    private void setMockServer(HttpMethod method, String url, String responseBody) {
        mockServer.expect(requestTo(startsWith(url)))
                .andExpect(method(method))
                .andRespond(MockRestResponseCreators.withSuccess(responseBody, MediaType.APPLICATION_JSON));
    }

    @Nested
    class SearchStores {

        @Test
        void 가게_검색을_할_수_있다() {
            String url = "https://dapi.kakao.com/v2/local/search/keyword.json";
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
            setMockServer(HttpMethod.GET, url, responseBody);

            List<MapClientStoreSearchResult> results = mapClient.searchStores("농민백암순대");

            MapClientStoreSearchResult result = results.getFirst();
            assertAll(
                    () -> assertThat(results).hasSize(1),
                    () -> assertThat(result.kakaoId()).isEqualTo("17163273"),
                    () -> assertThat(result.categoryGroupCode()).isEqualTo("FD6"),
                    () -> assertThat(result.categoryName()).isEqualTo("음식점 > 한식 > 국밥"),
                    () -> assertThat(result.phoneNumber()).isEqualTo("02-555-9603"),
                    () -> assertThat(result.name()).isEqualTo("농민백암순대 본점"),
                    () -> assertThat(result.placeUrl()).isEqualTo("http://place.map.kakao.com/17163273"),
                    () -> assertThat(result.lotNumberAddress()).isEqualTo("서울 강남구 대치동 896-33"),
                    () -> assertThat(result.roadAddress()).isEqualTo("서울 강남구 선릉로86길 40-4"),
                    () -> assertThat(result.latitude()).isEqualTo(37.503708148482524),
                    () -> assertThat(result.longitude()).isEqualTo(127.05300772497776)
            );
        }

        @Test
        void 검색_결과가_없을_경우_빈_리스트를_반환한다() {
            String url = "https://dapi.kakao.com/v2/local/search/keyword.json";
            String responseBody = """
                    {
                        "documents": [],
                        "meta": {
                            "is_end": true,
                            "pageable_count": 0,
                            "same_name": {
                                "keyword": "존재하지 않는 가게",
                                "region": [],
                                "selected_region": ""
                            },
                            "total_count": 0
                        }
                    }""";
            setMockServer(HttpMethod.GET, url, responseBody);

            List<MapClientStoreSearchResult> results = mapClient.searchStores("존재하지 않는 가게");

            assertThat(results).isEmpty();
        }
    }
}
