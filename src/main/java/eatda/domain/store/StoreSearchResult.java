package eatda.domain.store;

public record StoreSearchResult(
        String kakaoId,
        StoreCategory category,
        String phoneNumber,
        String name,
        String placeUrl,
        String lotNumberAddress,
        String roadAddress,
        double latitude,
        double longitude
) {

    public Store toStore() {
        return Store.builder()
                .kakaoId(kakaoId)
                .category(category)
                .phoneNumber(phoneNumber)
                .name(name)
                .placeUrl(placeUrl)
                .roadAddress(roadAddress)
                .lotNumberAddress(lotNumberAddress)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
