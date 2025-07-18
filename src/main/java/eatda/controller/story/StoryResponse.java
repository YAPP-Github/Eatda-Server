package eatda.controller.story;

public record StoryResponse(
        String storeKakaoId,
        String category,
        String storeName,
        String storeDistrict,
        String storeNeighborhood,
        String description,
        String imageUrl
) {
}
