package eatda.controller.story;

import org.springframework.lang.Nullable;

public record StoryResponse(
        @Nullable Long storeId,
        String storeKakaoId,
        String category,
        String storeName,
        String storeDistrict,
        String storeNeighborhood,
        String description,
        String imageUrl,
        long memberId,
        String memberNickname
) {

}
