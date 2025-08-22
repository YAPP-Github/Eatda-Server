package eatda.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseImageEntity {

    @Column(nullable = false, length = 511)
    private String imageKey;

    @Column(nullable = false)
    private long orderIndex;

    @Column(length = 255)
    private String contentType;

    private Long fileSize;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public BaseImageEntity(String imageKey, long orderIndex, String contentType, Long fileSize) {
        this.imageKey = imageKey;
        this.orderIndex = orderIndex;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }
}
