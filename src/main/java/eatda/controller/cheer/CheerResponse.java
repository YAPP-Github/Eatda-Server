package eatda.controller.cheer;

import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerTagName;
import eatda.domain.store.Store;
import java.util.List;

public record CheerResponse(
        long storeId,
        long cheerId,
        String imageUrl,
        String cheerDescription,
        List<CheerTagName> tags
) {

    public CheerResponse(Cheer cheer, Store store, String imageUrl) {
        this(
                store.getId(),
                cheer.getId(),
                imageUrl,
                cheer.getDescription(),
                cheer.getCheerTagNames()
        );
    }
}
