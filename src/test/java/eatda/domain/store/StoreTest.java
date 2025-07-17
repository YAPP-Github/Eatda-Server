package eatda.domain.store;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StoreTest {

    private static Store.StoreBuilder DEFAULT_BUILDER = Store.builder()
            .kakaoId("123456789")
            .category(StoreCategory.OTHER)
            .phoneNumber("010-1234-5678")
            .name("가게 이름")
            .placeUrl("https://place.kakao.com/123456789")
            .roadAddress("")
            .lotNumberAddress("서울특별시 강남구 역삼동 123-45")
            .latitude(37.5665)
            .longitude(126.978);

    @Nested
    class GetAddressDistrict {

        @Test
        void 주소_구_정보를_지번_주소에서_반환한다() {
            Store store = DEFAULT_BUILDER
                    .lotNumberAddress("서울특별시 성북구 석관동 123-45")
                    .build();

            String actual = store.getAddressDistrict();

            assertThat(actual).isEqualTo("성북구");
        }
    }

    @Nested
    class GetAddressNeighborhood {

        @Test
        void 주소_동_정보를_지번_주소에서_반환한다() {
            Store store = DEFAULT_BUILDER
                    .lotNumberAddress("서울특별시 성북구 석관동 123-45")
                    .build();

            String actual = store.getAddressNeighborhood();

            assertThat(actual).isEqualTo("석관동");
        }
    }
}
