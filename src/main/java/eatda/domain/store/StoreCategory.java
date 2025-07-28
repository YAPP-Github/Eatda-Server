package eatda.domain.store;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum StoreCategory {

    KOREAN("한식"),
    CHINESE("중식"),
    JAPANESE("일식"),
    WESTERN("양식"),
    CAFE("카페"),
    OTHER("기타");

    private final String categoryName;

    StoreCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public static StoreCategory from(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_CATEGORY);
        }
        return Arrays.stream(values())
                .filter(category -> category.categoryName.equals(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.INVALID_STORE_CATEGORY));
    }
}
