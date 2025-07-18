package eatda.controller.store;

import eatda.domain.store.Store;

public record StorePreviewResponse(
        long id,
        String imageUrl,
        String name,
        String district,
        String neighborhood,
        String category
) {

    public StorePreviewResponse(Store store, String imageUrl) {
        this(
                store.getId(),
                imageUrl,
                store.getName(),
                store.getAddressDistrict(),
                store.getAddressNeighborhood(),
                store.getCategory().getCategoryName()
        );
    }
}
