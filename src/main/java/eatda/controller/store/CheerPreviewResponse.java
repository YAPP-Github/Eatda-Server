package eatda.controller.store;

import eatda.domain.store.Cheer;
import eatda.domain.store.Store;

public record CheerPreviewResponse(
        long storeId,
        String storeImageUrl,
        String storeName,
        String storeDistrict,
        String storeNeighborhood,
        String storeCategory,
        long cheerId,
        String cheerDescription
) {

    public CheerPreviewResponse(Cheer cheer, Store store, String storeImageUrl) {
        this(
                store.getId(),
                storeImageUrl,
                store.getName(),
                store.getAddressDistrict(),
                store.getAddressNeighborhood(),
                store.getCategory().getCategoryName(),
                cheer.getId(),
                cheer.getDescription()
        );
    }
}
