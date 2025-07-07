package eatda.domain.store;

import java.util.regex.Pattern;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StorePhoneNumber {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^\\d{8,12}$");

    @Column(name = "phone_number", nullable = false)
    private String value;

    public StorePhoneNumber(String value) {
        validateNumber(value);
        this.value = value;
    }

    private void validateNumber(String number) {
        if (number != null && !PHONE_NUMBER_PATTERN.matcher(number).matches()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_PHONE_NUMBER);
        }
    }
}
