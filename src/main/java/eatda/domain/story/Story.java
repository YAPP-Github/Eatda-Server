package eatda.domain.story;

import eatda.domain.AuditingEntity;
import eatda.domain.member.Member;
import eatda.domain.store.StoreCategory;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Column(name = "store_road_address", nullable = false)
    private String storeRoadAddress;

    @Column(name = "store_lot_number_address", nullable = false)
    private String storeLotNumberAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "store_category", nullable = false)
    private StoreCategory storeCategory;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image_key", nullable = false)
    private String imageKey;

    @Builder
    private Story(
            Member member,
            String storeKakaoId,
            StoreCategory storeCategory,
            String storeName,
            String storeRoadAddress,
            String storeLotNumberAddress,
            String description,
            String imageKey
    ) {
        validateMember(member);
        validateStore(storeKakaoId, storeCategory, storeName, storeRoadAddress, storeLotNumberAddress);
        validateStory(description, imageKey);

        this.member = member;
        this.storeKakaoId = storeKakaoId;
        this.storeCategory = storeCategory;
        this.storeName = storeName;
        this.storeRoadAddress = storeRoadAddress;
        this.storeLotNumberAddress = storeLotNumberAddress;
        this.description = description;
        this.imageKey = imageKey;
    }

    private void validateMember(Member member) {
        if (member == null) {
            throw new BusinessException(BusinessErrorCode.STORY_MEMBER_REQUIRED);
        }
    }

    private void validateStore(
            String storeKakaoId,
            StoreCategory storeCategory,
            String storeName,
            String roadAddress,
            String lotNumberAddress
    ) {
        validateStoreKakaoId(storeKakaoId);
        validateStoreCategory(storeCategory);
        validateStoreName(storeName);
        validateStoreRoadAddress(roadAddress);
        validateStoreLotNumberAddress(lotNumberAddress);
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

    private void validateStoreRoadAddress(String roadAddress) {
        if (roadAddress == null || roadAddress.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_ADDRESS);
        }
    }

    private void validateStoreLotNumberAddress(String lotNumberAddress) {
        if (lotNumberAddress == null || lotNumberAddress.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_ADDRESS);
        }
    }

    private void validateStoreCategory(StoreCategory storeCategory) {
        if (storeCategory == null) {
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

    public String getAddressDistrict() {
        String[] addressParts = storeLotNumberAddress.split(" ");
        if (addressParts.length < 2) {
            return "";
        }
        return addressParts[1];
    }

    public String getAddressNeighborhood() {
        String[] addressParts = storeLotNumberAddress.split(" ");
        if (addressParts.length < 3) {
            return "";
        }
        return addressParts[2];
    }
}
