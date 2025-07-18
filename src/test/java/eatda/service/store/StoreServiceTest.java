package eatda.service.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import eatda.client.map.StoreSearchResult;
import eatda.service.BaseServiceTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StoreServiceTest extends BaseServiceTest {

    @Autowired
    private StoreService storeService;

    @BeforeEach
    void mockingClient() {
        List<StoreSearchResult> searchResults = List.of(
                new StoreSearchResult("123", "FD6", "음식점 > 한식 > 국밥", "010-1234-1234", "농민백암순대 본점", "https://yapp.co.kr",
                        "서울 강남구 대치동 896-33", "서울 강남구 선릉로86길 40-4", 37.0d, 128.0d),
                new StoreSearchResult("456", "FD6", "음식점 > 한식 > 국밥", "010-1234-1234", "농민백암순대 시청점", "http://yapp.kr",
                        "서울 중구 북창동 19-4", null, 37.0d, 128.0d)
        );

        doReturn(searchResults).when(mapClient).searchShops(anyString());
    }

    @Nested
    class SearchStores {

        @Test
        void 음식점_검색_결과를_반환한다() {
            String query = "농민백암순대";

            var response = storeService.searchStores(query);

            assertAll(
                    () -> assertThat(response.stores()).hasSize(2),
                    () -> assertThat(response.stores().get(0).kakaoId()).isEqualTo("123"),
                    () -> assertThat(response.stores().get(0).address()).isEqualTo("서울 강남구 대치동 896-33"),
                    () -> assertThat(response.stores().get(1).kakaoId()).isEqualTo("456"),
                    () -> assertThat(response.stores().get(1).address()).isEqualTo("서울 중구 북창동 19-4")
            );
        }
    }
}
