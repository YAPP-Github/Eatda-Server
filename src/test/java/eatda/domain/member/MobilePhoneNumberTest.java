package eatda.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;

class MobilePhoneNumberTest {

    @Nested
    @DisplayName("휴대폰 번호를 생성할 때")
    class CreateMobilePhoneNumberTest {

        @Test
        void 휴대폰_번호를_정상적으로_생성한다() {
            String validPhoneNumber = "01012345678";

            MobilePhoneNumber phoneNumber = new MobilePhoneNumber(validPhoneNumber);

            assertThat(phoneNumber.getValue()).isEqualTo(validPhoneNumber);
        }

        @Test
        void null_값으로_생성할_수_있다() {
            String nullNumber = null;

            MobilePhoneNumber phoneNumber = new MobilePhoneNumber(nullNumber);

            assertThat(phoneNumber.getValue()).isNull();
        }

        @ParameterizedTest
        @ValueSource(strings = {"0101234567", "010123456789", "010-1234-5678", "abcdefghijk"})
        void 번호_형식이_올바르지_않으면_예외를_던진다(String invalidNumber) {
            BusinessException exception = assertThrows(BusinessException.class, () -> new MobilePhoneNumber(invalidNumber));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_MOBILE_PHONE_NUMBER);
        }
    }

    @Nested
    class isSameValue {

        @Test
        void 동일한_값을_비교하면_true를_반환한다() {
            MobilePhoneNumber phoneNumber = new MobilePhoneNumber("01012345678");

            boolean result = phoneNumber.isSame("01012345678");

            assertThat(result).isTrue();
        }

        @Test
        void 다른_값을_비교하면_false를_반환한다() {
            MobilePhoneNumber phoneNumber = new MobilePhoneNumber("01012345678");

            boolean result = phoneNumber.isSame("01012345679");

            assertThat(result).isFalse();
        }
    }
}
