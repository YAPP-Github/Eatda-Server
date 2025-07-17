package eatda.controller.store;

import eatda.domain.store.Cheer;
import eatda.domain.store.Store;

public record CheerPreviewResponse(
        long storeId,
        String imageUrl,
        String storeName,
        String storeDistrict,
        String storeNeighborhood,
        String storeCategory,
        long cheerId,
        String cheerDescription
) {

    public CheerPreviewResponse(Cheer cheer, Store store, String imageUrl) {
        this(
                store.getId(),
                imageUrl,
                store.getName(),
                store.getAddressDistrict(),
                store.getAddressNeighborhood(),
                store.getCategory().getCategoryName(),
                cheer.getId(),
                cheer.getDescription()
        );
    }
}
