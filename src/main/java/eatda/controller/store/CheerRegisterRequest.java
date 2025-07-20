package eatda.controller.store;

public record CheerRegisterRequest(
        String storeKakaoId,
        String storeName,
        String description
) {
}
