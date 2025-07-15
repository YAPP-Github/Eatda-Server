package eatda.controller.story;

public record StoryRegisterRequest(
        String storeKakaoId,
        String storeName,
        String storeAddress,
        String category,
        String description
) {
}
