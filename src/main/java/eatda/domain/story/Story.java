package eatda.domain.story;

import eatda.domain.AuditingEntity;
import eatda.domain.member.Member;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "story")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Story extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "store_kakao_id", nullable = false)
    private String storeKakaoId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "store_address", nullable = false)
    private String storeAddress;

    @Column(name = "store_category", nullable = false)
    private String storeCategory;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image_key", nullable = false)
    private String imageKey;

    @Builder
    public Story(
            Member member,
            String storeKakaoId,
            String storeName,
            String storeAddress,
            String storeCategory,
            String description,
            String imageKey
    ) {
        validateMember(member);
        validateStore(storeKakaoId, storeName, storeAddress, storeCategory);
        validateStory(description, imageKey);

        this.member = member;
        this.storeKakaoId = storeKakaoId;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeCategory = storeCategory;
        this.description = description;
        this.imageKey = imageKey;
    }

    private void validateMember(Member member) {
        if (member == null) {
            throw new BusinessException(BusinessErrorCode.STORY_MEMBER_REQUIRED);
        }
    }

    private void validateStore(String storeKakaoId, String storeName, String storeAddress, String storeCategory) {
        validateStoreKakaoId(storeKakaoId);
        validateStoreName(storeName);
        validateStoreAddress(storeAddress);
        validateStoreCategory(storeCategory);
    }

    private void validateStory(String description, String imageKey) {
        validateDescription(description);
        validateImage(imageKey);
    }

    private void validateStoreKakaoId(String storeKakaoId) {
        if (storeKakaoId == null || storeKakaoId.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_KAKAO_ID);
        }
    }

    private void validateStoreName(String storeName) {
        if (storeName == null || storeName.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_NAME);
        }
    }

    private void validateStoreAddress(String storeAddress) {
        if (storeAddress == null || storeAddress.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_ADDRESS);
        }
    }

    private void validateStoreCategory(String storeCategory) {
        if (storeCategory == null || storeCategory.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_CATEGORY);
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORY_DESCRIPTION);
        }
    }

    private void validateImage(String imageKey) {
        if (imageKey == null || imageKey.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORY_IMAGE_KEY);
        }
    }
}
