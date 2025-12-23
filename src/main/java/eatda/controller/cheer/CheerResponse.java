package eatda.controller.cheer;

import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerTagName;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public record CheerResponse(
        long storeId,
        long cheerId,
        List<CheerImageResponse> images,
        String cheerDescription,
        List<CheerTagName> tags
) {

    public CheerResponse(Cheer cheer, String cdnBaseUrl) {
        this(
                cheer.getStore().getId(),
                cheer.getId(),
                cheer.getImages().stream()
                        .map(img -> new CheerImageResponse(img, cdnBaseUrl))
                        .sorted(Comparator.comparingLong(CheerImageResponse::orderIndex))
                        .collect(Collectors.toList()),
                cheer.getDescription(),
                cheer.getCheerTagNames()
        );
    }
}
