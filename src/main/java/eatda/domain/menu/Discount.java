package eatda.domain.menu;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Discount {
    private static final int MIN_PRICE = 1;

    private Integer discountPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Discount(Price originalPrice, Integer discountPrice, LocalDateTime startTime, LocalDateTime endTime) {
        validatePrice(originalPrice, discountPrice);
        validateTime(startTime, endTime);
        this.discountPrice = discountPrice;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private void validatePrice(Price originalPrice, Integer discountPrice) {
        if (discountPrice != null) {
            if (discountPrice < MIN_PRICE || discountPrice >= originalPrice.getValue()) {
                throw new BusinessException(BusinessErrorCode.INVALID_MENU_DISCOUNT_PRICE);
            }
        }
    }

    private void validateTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new BusinessException(BusinessErrorCode.INVALID_MENU_DISCOUNT_TIME);
        }
    }
}

