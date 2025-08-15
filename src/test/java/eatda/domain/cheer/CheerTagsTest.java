package eatda.domain.cheer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eatda.domain.ImageKey;
import eatda.domain.member.Member;
import eatda.domain.store.District;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheerTagsTest {

    private static final Member DEFAULT_MEMBER = new Member("socialId", "email@kakao.com", "nickname");
    private static final Store DEFAULT_STORE = Store.builder()
            .kakaoId("123456789")
            .category(StoreCategory.OTHER)
            .phoneNumber("010-1234-5678")
            .name("가게 이름")
            .placeUrl("https://place.kakao.com/123456789")
            .roadAddress("")
            .lotNumberAddress("서울특별시 강남구 역삼동 123-45")
            .district(District.GANGNAM)
            .latitude(37.5665)
            .longitude(126.978)
            .build();
    private static final Cheer DEFAULT_CHEER = new Cheer(DEFAULT_MEMBER, DEFAULT_STORE, "Great store!",
            new ImageKey("imageKey"));

    @Nested
    class SetTags {

        @Test
        void 각_카테고리별_태그는_최대_개수가_정해져있다() {
            List<CheerTagName> tagNames = List.of(
                    CheerTagName.OLD_STORE_MOOD, CheerTagName.ENERGETIC,
                    CheerTagName.GROUP_RESERVATION, CheerTagName.LARGE_PARKING);
            CheerTags cheerTags = new CheerTags();

            assertThatCode(() -> cheerTags.setTags(DEFAULT_CHEER, tagNames)).doesNotThrowAnyException();
        }

        @Test
        void 태그_이름은_비어있을_수_있다() {
            List<CheerTagName> tagNames = Collections.emptyList();
            CheerTags cheerTags = new CheerTags();

            assertThatCode(() -> cheerTags.setTags(DEFAULT_CHEER, tagNames)).doesNotThrowAnyException();
        }

        @Test
        void 카테고리별_태그는_최대_개수를_초과할_수_없다() {
            List<CheerTagName> tagNames = List.of(
                    CheerTagName.OLD_STORE_MOOD, CheerTagName.ENERGETIC, CheerTagName.GOOD_FOR_DATING);
            CheerTags cheerTags = new CheerTags();

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> cheerTags.setTags(DEFAULT_CHEER, tagNames));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.EXCEED_CHEER_TAGS_PER_TYPE);
        }

        @Test
        void 태그_이름은_중복될_수_없다() {
            List<CheerTagName> tagNames = List.of(CheerTagName.OLD_STORE_MOOD, CheerTagName.OLD_STORE_MOOD);
            CheerTags cheerTags = new CheerTags();

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> cheerTags.setTags(DEFAULT_CHEER, tagNames));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.CHEER_TAGS_DUPLICATED);
        }
    }

}
