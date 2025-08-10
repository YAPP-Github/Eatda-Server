package eatda.service.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import eatda.client.map.MapClientStoreSearchResult;
import eatda.domain.store.StoreCategory;
import eatda.domain.store.StoreSearchResult;
import eatda.service.BaseServiceTest;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StoreSearchServiceTest extends BaseServiceTest {

    @Nested
    class SearchStoreByKakaoId {

        @Test
        void 음식점_카카오_ID로_검색_결과를_반환한다() {
            mockingMapClient();
            String query = "농민백암순대";
            String kakaoId = "123";

            StoreSearchResult response = storeSearchService.searchStoreByKakaoId(query, kakaoId);

            assertAll(
                    () -> assertThat(response.kakaoId()).isEqualTo("123"),
                    () -> assertThat(response.category()).isEqualTo(StoreCategory.KOREAN),
                    () -> assertThat(response.lotNumberAddress()).isEqualTo("서울 강남구 대치동 896-33")
            );
        }
    }

    @Nested
    class SearchStores {

        @Test
        void 음식점_검색_결과를_반환한다() {
            mockingMapClient();
            String query = "농민백암순대";

            List<StoreSearchResult> response = storeSearchService.searchStores(query);

            assertAll(
                    () -> assertThat(response.get(0).kakaoId()).isEqualTo("123"),
                    () -> assertThat(response.get(0).category()).isEqualTo(StoreCategory.KOREAN),
                    () -> assertThat(response.get(0).lotNumberAddress()).isEqualTo("서울 강남구 대치동 896-33"),
                    () -> assertThat(response.get(1).kakaoId()).isEqualTo("456"),
                    () -> assertThat(response.get(1).category()).isEqualTo(StoreCategory.KOREAN),
                    () -> assertThat(response.get(1).lotNumberAddress()).isEqualTo("서울 중구 북창동 19-4")
            );
        }
    }

    void mockingMapClient() {
        List<MapClientStoreSearchResult> searchResults = List.of(
                new MapClientStoreSearchResult("123", "FD6", "음식점 > 한식 > 국밥", "010-1234-1234", "농민백암순대 본점",
                        "https://yapp.co.kr", "서울 강남구 대치동 896-33", "서울 강남구 선릉로86길 40-4", 37.0d, 128.0d),
                new MapClientStoreSearchResult("456", "FD6", "음식점 > 한식 > 국밥", "010-1234-1234", "농민백암순대 시청점",
                        "http://yapp.kr", "서울 중구 북창동 19-4", null, 37.0d, 128.0d)
        );
        doReturn(searchResults).when(mapClient).searchStores(anyString());
    }
}
