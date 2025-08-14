package eatda.domain.cheer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheerTagNamesTest {

    @Nested
    class Validate {

        @Test
        void 각_카테고리별_태그는_최대_개수가_정해져있다() {
            List<CheerTagName> tagNames = List.of(
                    CheerTagName.OLD_STORE_MOOD, CheerTagName.ENERGETIC,
                    CheerTagName.GROUP_RESERVATION, CheerTagName.LARGE_PARKING);

            assertThatCode(() -> new CheerTagNames(tagNames)).doesNotThrowAnyException();
        }

        @Test
        void 태그_이름은_비어있을_수_있다() {
            List<CheerTagName> tagNames = Collections.emptyList();

            assertThatCode(() -> new CheerTagNames(tagNames)).doesNotThrowAnyException();
        }

        @Test
        void 카테고리별_태그는_최대_개수를_초과할_수_없다() {
            List<CheerTagName> tagNames = List.of(
                    CheerTagName.OLD_STORE_MOOD, CheerTagName.ENERGETIC, CheerTagName.GOOD_FOR_DATING);

            BusinessException exception = assertThrows(BusinessException.class, () -> new CheerTagNames(tagNames));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.EXCEED_CHEER_TAGS_PER_TYPE);
        }

        @Test
        void 태그_이름은_중복될_수_없다() {
            List<CheerTagName> tagNames = List.of(CheerTagName.OLD_STORE_MOOD, CheerTagName.OLD_STORE_MOOD);

            BusinessException exception = assertThrows(BusinessException.class, () -> new CheerTagNames(tagNames));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.CHEER_TAGS_DUPLICATED);
        }
    }
}
