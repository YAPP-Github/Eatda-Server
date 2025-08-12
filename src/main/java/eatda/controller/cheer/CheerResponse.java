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

    public CheerResponse(Cheer cheer, String imageUrl, Store store) {
        this(
                store.getId(),
                cheer.getId(),
                imageUrl,
                cheer.getDescription(),
                List.of() // TODO tags 불러오기
        );
    }
}
