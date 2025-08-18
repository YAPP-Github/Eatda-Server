package eatda.controller.cheer;

import eatda.domain.cheer.Cheer;
import eatda.domain.store.Store;
import java.util.List;
import eatda.domain.cheer.CheerTagName;
import java.util.List;

public record CheerPreviewResponse(
        long storeId,
        List<CheerImageResponse> images,
        String storeName,
        String storeDistrict,
        String storeNeighborhood,
        String storeCategory,
        long cheerId,
        String cheerDescription,
        List<CheerTagName> tags,
        long memberId,
        String memberNickname
) {

    public CheerPreviewResponse(Cheer cheer, List<CheerImageResponse> images) {
        this(
                cheer.getStore().getId(),
                images,
                cheer.getStore().getName(),
                cheer.getStore().getAddressDistrict(),
                cheer.getStore().getAddressNeighborhood(),
                cheer.getStore().getCategory().getCategoryName(),
                cheer.getId(),
                cheer.getDescription(),
                cheer.getCheerTagNames(),
                cheer.getMember().getId(),
                cheer.getMember().getNickname()
        );
    }
}
