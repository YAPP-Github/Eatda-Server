package eatda.domain.story;

import eatda.domain.BaseImageEntity;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "story_image")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoryImage extends BaseImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    public StoryImage(Story story, String imageKey, long orderIndex, String contentType, Long fileSize) {
        super(
                validateImageKey(imageKey),
                orderIndex,
                validateContentType(contentType),
                validateFileSize(fileSize)
        );
        this.story = story;
    }

    private static String validateImageKey(String imageKey) {
        if (imageKey == null || imageKey.trim().isEmpty()) {
            throw new BusinessException(BusinessErrorCode.INVALID_EMPTY_FILE_DETAILS);
        }
        return imageKey;
    }

    private static String validateContentType(String contentType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new BusinessException(BusinessErrorCode.INVALID_IMAGE_TYPE);
        }
        return contentType;
    }

    private static Long validateFileSize(Long fileSize) {
        if (fileSize == null || fileSize <= 0) {
            throw new BusinessException(BusinessErrorCode.INVALID_MAX_FILE_SIZE);
        }
        return fileSize;
    }

    protected void setStory(Story story) {
        this.story = story;
    }
}
