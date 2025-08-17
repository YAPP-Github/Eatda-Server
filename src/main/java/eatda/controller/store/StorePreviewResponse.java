package eatda.controller.store;

import eatda.domain.store.Store;
import java.util.List;

public record StorePreviewResponse(
        long id,
        String imageUrl,
        String name,
        String district,
        String neighborhood,
        String category,
        List<String> cheerDescriptions
) {

    public StorePreviewResponse(Store store, String imageUrl) {
        this(
                store.getId(),
                imageUrl,
                store.getName(),
                store.getAddressDistrict(),
                store.getAddressNeighborhood(),
                store.getCategory().getCategoryName(),
                store.getCheerDescriptions()
        );
    }
}
