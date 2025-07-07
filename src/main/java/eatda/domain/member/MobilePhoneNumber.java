package eatda.domain.member;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MobilePhoneNumber {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^\\d{11}$");

    @Column(name = "phone_number")
    private String value;

    public MobilePhoneNumber(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String number) {
        if (number != null && !PHONE_NUMBER_PATTERN.matcher(number).matches()) {
            throw new BusinessException(BusinessErrorCode.INVALID_MOBILE_PHONE_NUMBER);
        }
    }

    public boolean isSame(String value) {
        return this.value.equals(value);
    }
}
