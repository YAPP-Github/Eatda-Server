package eatda.controller.cheer;

import eatda.domain.cheer.Cheer;
import eatda.domain.store.Store;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public record CheerResponse(
        long storeId,
        long cheerId,
        List<CheerImageResponse> images,
        String cheerDescription
) {

    public CheerResponse(Cheer cheer, Store store) {
        this(
                store.getId(),
                cheer.getId(),
                cheer.getImages().stream()
                        .map(CheerImageResponse::new)
                        .sorted(Comparator.comparingLong(CheerImageResponse::orderIndex))
                        .collect(Collectors.toList()),
                cheer.getDescription()
        );
    }
}
