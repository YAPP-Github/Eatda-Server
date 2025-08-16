package eatda.controller.cheer;

import eatda.domain.cheer.Cheer;
import eatda.domain.store.Store;
import java.util.List;

public record CheerPreviewResponse(
        long storeId,
        List<CheerImageResponse> images,
        String storeName,
        String storeDistrict,
        String storeNeighborhood,
        String storeCategory,
        long cheerId,
        String cheerDescription
) {

    public CheerPreviewResponse(Cheer cheer, Store store, List<CheerImageResponse> images) {
        this(
                store.getId(),
                images,
                store.getName(),
                store.getAddressDistrict(),
                store.getAddressNeighborhood(),
                store.getCategory().getCategoryName(),
                cheer.getId(),
                cheer.getDescription()
        );
    }
}
