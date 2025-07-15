package eatda.controller.story;

public record StoryResponse(
        String storeKakaoId,
        String category,
        String storeName,
        String storeAddress,
        String description,
        String imageUrl
) {
}
