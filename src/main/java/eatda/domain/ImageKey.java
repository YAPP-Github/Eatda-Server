package eatda.domain;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageKey {

    @Column(name = "image_key", nullable = false)
    private String value;

    public ImageKey(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_IMAGE_KEY);
        }
        this.value = value;
    }
}
