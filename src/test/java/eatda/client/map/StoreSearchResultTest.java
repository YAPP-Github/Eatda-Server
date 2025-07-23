package eatda.client.map;

import static org.assertj.core.api.Assertions.assertThat;

import eatda.domain.store.StoreCategory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StoreSearchResultTest {

    @Nested
    class GetStoreCategory {

        @Test
        void 제공된_카테고리_이름에_맞는_음식점_카테고리를_반환한다() {
            StoreSearchResult store = new StoreSearchResult(
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
            StoreSearchResult store = new StoreSearchResult(
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
}
