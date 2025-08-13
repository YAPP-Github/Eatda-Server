package eatda.client.map;

import static org.assertj.core.api.Assertions.assertThat;

import eatda.domain.store.StoreCategory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MapClientStoreSearchResultTest {

    @Nested
    class GetStoreCategory {

        @Test
        void 제공된_카테고리_이름에_맞는_음식점_카테고리를_반환한다() {
            MapClientStoreSearchResult store = new MapClientStoreSearchResult(
                    "1062153333",
                    "FD6",
                    "음식점 > 한식 > 순대",
                    "02-755-5232",
                    "농민백암순대 시청직영점",
                    "http://place.map.kakao.com/1062153333",
                    "서울 중구 북창동 19-4",
                    "서울 중구 남대문로1길 33",
                    37.56259825108099,
                    126.97715943361476
            );
            StoreCategory category = store.getStoreCategory();

            assertThat(category).isEqualTo(StoreCategory.KOREAN);
        }

        @Test
        void 특정_카테고리에_없을_경우_기타_카테고리를_반환한다() {
            MapClientStoreSearchResult store = new MapClientStoreSearchResult(
                    "1062153333",
                    "FD6",
                    "음식점 > 기타",
                    "02-755-5232",
                    "농민백암순대 시청직영점",
                    "http://place.map.kakao.com/1062153333",
                    "서울 중구 북창동 19-4",
                    "서울 중구 남대문로1길 33",
                    37.56259825108099,
                    126.97715943361476
            );
            StoreCategory category = store.getStoreCategory();

            assertThat(category).isEqualTo(StoreCategory.OTHER);
        }
    }

    @Nested
    class GetDistrict {

        @Test
        void 제공된_지번_주소에_따라_해당_구를_반환한다() {
            MapClientStoreSearchResult store = new MapClientStoreSearchResult(
                    "1062153333",
                    "FD6",
                    "음식점 > 한식 > 순대",
                    "02-755-5232",
                    "농민백암순대 시청직영점",
                    "http://place.map.kakao.com/1062153333",
                    "서울 중구 북창동 19-4",
                    "서울 중구 남대문로1길 33",
                    37.56259825108099,
                    126.97715943361476
            );

            assertThat(store.getDistrict()).isEqualTo(eatda.domain.store.District.JUNG);
        }

        @ValueSource(strings = {" ", "\t", "이것은 주소가 아닙니다.", "empty"})
        @NullAndEmptySource
        @ParameterizedTest
        void 지번_주소가_정해진_형식이_아니면_ETC를_반환한다(String lotNumberAddress) {
            MapClientStoreSearchResult store = new MapClientStoreSearchResult(
                    "1062153333",
                    "FD6",
                    "음식점 > 한식 > 순대",
                    "02-755-5232",
                    "농민백암순대 시청직영점",
                    "http://place.map.kakao.com/1062153333",
                    lotNumberAddress,
                    null,
                    37.56259825108099,
                    126.97715943361476
            );

            assertThat(store.getDistrict()).isEqualTo(eatda.domain.store.District.ETC);
        }
    }
}
