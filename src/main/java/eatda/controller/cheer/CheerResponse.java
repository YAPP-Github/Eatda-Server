package eatda.controller.cheer;

import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerTagName;
import eatda.domain.store.Store;
import java.util.List;

public record CheerResponse(
        long storeId,
        long cheerId,
        List<CheerImageResponse> images,
        String cheerDescription,
        List<CheerTagName> tags
) {

    public CheerResponse(Cheer cheer, Store store, String cdnBaseUrl) {
        this(
                store.getId(),
                cheer.getId(),
                cheer.getImages().stream()
                        .map(img -> new CheerImageResponse(img, cdnBaseUrl)) // ✅ CDN 붙여줌
                        .sorted(Comparator.comparingLong(CheerImageResponse::orderIndex))
                        .collect(Collectors.toList()),
                cheer.getDescription(),
                cheer.getCheerTagNames()
        );
    }
}
