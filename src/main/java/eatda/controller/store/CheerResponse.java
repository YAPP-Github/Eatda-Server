package eatda.controller.store;

import eatda.domain.store.Cheer;
import eatda.domain.store.Store;

public record CheerResponse(
        long storeId,
        long cheerId,
        String imageUrl,
        String cheerDescription
) {

    public CheerResponse(Cheer cheer, String imageUrl, Store store) {
        this(
                store.getId(),
                cheer.getId(),
                imageUrl,
                cheer.getDescription()
        );
    }
}
