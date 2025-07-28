package eatda.controller.store;

import eatda.domain.store.Store;

public record StoreResponse(
        long id,
        String kakaoId,
        String name,
        String district,
        String neighborhood,
        String category,
        String placeUrl
) {

    public StoreResponse(Store store) {
        this(
                store.getId(),
                store.getKakaoId(),
                store.getName(),
                store.getAddressDistrict(),
                store.getAddressNeighborhood(),
                store.getCategory().getCategoryName(),
                store.getPlaceUrl()
        );
    }
}
