package eatda.domain.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eatda.client.map.MapClientStoreSearchResult;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StoreSearchFilterTest {

    private final StoreSearchFilter storeSearchFilter = new StoreSearchFilter();

    @Nested
    class FilterSearchedStores {

        @Test
        void 빈_검색_결과를_넣으면_빈_리스트를_반환한다() {
            List<StoreSearchResult> actual = storeSearchFilter.filterSearchedStores(List.of());

            assertThat(actual).isEmpty();
        }

        @Test
        void 음식점이_아니거나_서울에_위치하지_않는_가게는_제외한다() {
            MapClientStoreSearchResult store1 = createStore("1", "서울음식점1", "FD6", "서울 강남구 대치동 896-33");
            MapClientStoreSearchResult store2 = createStore("2", "카페", "CD2", "서울 강남구 대치동 896-33");
            MapClientStoreSearchResult store3 = createStore("3", "서울음식점2", "FD6", "서울 강남구 대치동 896-33");
            MapClientStoreSearchResult store4 = createStore("4", "부산음식점", "FD6", "부산 연제구 연산동 632-8");

            List<MapClientStoreSearchResult> searchResults = List.of(store1, store2, store3, store4);
            List<StoreSearchResult> actual = storeSearchFilter.filterSearchedStores(searchResults);

            assertThat(actual).extracting(StoreSearchResult::kakaoId)
                    .containsExactlyInAnyOrder("1", "3");
        }

    }

    @Nested
    class FilterStoreByKakaoId {

        @Test
        void 음식점_카카오_ID로_검색_결과를_반환한다() {
            MapClientStoreSearchResult store1 = createStore("1", "서울음식점1", "FD6", "서울 강남구 대치동 896-33");
            MapClientStoreSearchResult store2 = createStore("2", "서울음식점2", "FD6", "서울 강남구 대치동 896-33");

            List<MapClientStoreSearchResult> searchResults = List.of(store1, store2);
            StoreSearchResult actual = storeSearchFilter.filterStoreByKakaoId(searchResults, "1");

            assertAll(
                    () -> assertThat(actual.kakaoId()).isEqualTo("1"),
                    () -> assertThat(actual.name()).isEqualTo("서울음식점1"),
                    () -> assertThat(actual.lotNumberAddress()).isEqualTo("서울 강남구 대치동 896-33")

            );
        }

        @Test
        void 존재하지_않는_카카오_ID로_검색하면_예외를_던진다() {
            MapClientStoreSearchResult store = createStore("1", "서울음식점1", "FD6", "서울 강남구 대치동 896-33");
            List<MapClientStoreSearchResult> searchResults = List.of(store);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> storeSearchFilter.filterStoreByKakaoId(searchResults, "999"));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.STORE_NOT_FOUND);
        }
    }

    private MapClientStoreSearchResult createStore(String id, String name, String categoryGroupCode,
                                                   String location) {
        return new MapClientStoreSearchResult(id, categoryGroupCode, "음식점 > 식당", "010-1234-1234", name,
                "https://yapp.co.kr", location, null, 37.0d, 128.0d);
    }
}
