package eatda.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageKey {

    @Column(name = "image_key", length = 511)
    private String value;

    public ImageKey(String value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return value == null || value.isBlank();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ImageKey imageKey = (ImageKey) object;
        return Objects.equals(value, imageKey.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
