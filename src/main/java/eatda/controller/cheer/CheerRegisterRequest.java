package eatda.controller.cheer;

public record CheerRegisterRequest(
        String storeKakaoId,
        String storeName,
        String description
) {
}
