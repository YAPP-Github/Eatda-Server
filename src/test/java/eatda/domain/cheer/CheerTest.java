package eatda.domain.cheer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eatda.domain.member.Member;
import eatda.domain.store.District;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class CheerTest {

    private static final Member DEFAULT_MEMBER = new Member("socialId", "email@kakao.com", "nickname");
    private static final Store DEFAULT_STORE = Store.builder()
            .kakaoId("1234567890")
            .category(StoreCategory.CAFE)
            .phoneNumber("02-1234-5678")
            .name("Test Store")
            .placeUrl("https://place.kakao.com/1234567890")
            .roadAddress("서울시 성북구 대학로 1길 1")
            .lotNumberAddress("서울시 성북구 동선동 1-1")
            .district(District.SEONGBUK)
            .latitude(37.5665)
            .longitude(126.978)
            .build();

    @Nested
    class Validate {

        @ParameterizedTest
        @NullAndEmptySource
        void 설명이_비어있으면_안된다(String description) {
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> new Cheer(DEFAULT_MEMBER, DEFAULT_STORE, description));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_CHEER_DESCRIPTION);
        }

        @Test
        void 기본_생성자는_예외를_던지지_않는다() {
            assertThatCode(() -> new Cheer(DEFAULT_MEMBER, DEFAULT_STORE, "Great store!"))
                    .doesNotThrowAnyException();
        }

        @Test
        void isAdmin_파라미터와_함께_생성할_수_있다() {
            assertThatCode(() -> new Cheer(DEFAULT_MEMBER, DEFAULT_STORE, "Great store!", true))
                    .doesNotThrowAnyException();
        }
    }
}
