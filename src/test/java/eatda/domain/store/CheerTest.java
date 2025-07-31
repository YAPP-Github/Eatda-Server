package eatda.domain.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eatda.domain.ImageKey;
import eatda.domain.member.Member;
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
            .latitude(37.5665)
            .longitude(126.978)
            .build();

    @Nested
    class Validate {

        @ParameterizedTest
        @NullAndEmptySource
        void 설명이_비어있으면_안된다(String description) {
            ImageKey imageKey = new ImageKey("imageKey");

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> new Cheer(DEFAULT_MEMBER, DEFAULT_STORE, description, imageKey));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_CHEER_DESCRIPTION);
        }

        @Test
        void 이미지_키는_null이_가능하다() {
            assertThatCode(() -> new Cheer(DEFAULT_MEMBER, DEFAULT_STORE, "Great store!", null))
                    .doesNotThrowAnyException();
        }
    }
}
