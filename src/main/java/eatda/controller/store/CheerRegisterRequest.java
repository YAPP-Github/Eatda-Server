package eatda.controller.store;

public record CheerRegisterRequest(
        String kakaoId,
        String storeName,
        String description
) {
}
