package eatda.controller.story;

public record StoryRegisterRequest(
        String storeName,
        String storeKakaoId,
        String description
) {
}
