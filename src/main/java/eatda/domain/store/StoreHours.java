package eatda.domain.store;

import java.time.LocalTime;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreHours {

    private LocalTime openTime;
    private LocalTime closeTime;

    public StoreHours(LocalTime openTime, LocalTime closeTime) {
        validate(openTime, closeTime);
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    private void validate(LocalTime openTime, LocalTime closeTime) {
        validateNotNull(openTime, closeTime);
        validateOrder(openTime, closeTime);
    }

    private void validateNotNull(LocalTime openTime, LocalTime closeTime) {
        if (openTime == null || closeTime == null) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_TIME_NULL);
        }
    }

    private void validateOrder(LocalTime openTime, LocalTime closeTime) {
        if (openTime.isAfter(closeTime)) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_TIME_ORDER);
        }
    }
}
