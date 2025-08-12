package eatda.controller.cheer;

import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerTag;
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

    public CheerResponse(Cheer cheer, List<CheerTag> cheerTags, Store store, String imageUrl) {
        this(
                store.getId(),
                cheer.getId(),
                imageUrl,
                cheer.getDescription(),
                toTagNames(cheerTags)
        );
    }

    private static List<CheerTagName> toTagNames(List<CheerTag> cheerTags) {
        return cheerTags.stream()
                .map(CheerTag::getName)
                .toList();
    }
}
