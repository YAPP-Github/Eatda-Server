package eatda.domain.store;

public record StoreSearchResult(
        String kakaoId,
        StoreCategory category,
        String phoneNumber,
        String name,
        String placeUrl,
        String lotNumberAddress,
        String roadAddress,
        District district,
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
                .lotNumberAddress(lotNumberAddress)
                .roadAddress(roadAddress)
                .district(district)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
