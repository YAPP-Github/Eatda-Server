package eatda.domain.story;

import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "story")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image_key", nullable = false)
    private String imageKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Story(Member member, Store store, String description, String imageUrl) {
        validate(member, store, description, imageUrl);
        this.member = member;
        this.store = store;
        this.description = description;
        this.imageKey = imageUrl;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    private void validate(Member member, Store store, String description, String imageUrl) {
        validateMember(member);
        validateStore(store);
        validateDescription(description);
        validateImage(imageUrl);
    }

    private void validateMember(Member member) {
        if (member == null) {
            throw new BusinessException(BusinessErrorCode.STORY_MEMBER_REQUIRED);
        }
    }

    private void validateStore(Store store) {
        if (store == null) {
            throw new BusinessException(BusinessErrorCode.STORY_STORE_REQUIRED);
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORY_DESCRIPTION);
        }
    }

    private void validateImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORY_IMAGE_URL);
        }
    }

}
