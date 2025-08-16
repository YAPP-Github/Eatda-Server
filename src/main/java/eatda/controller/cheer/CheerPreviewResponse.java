package eatda.controller.cheer;

import eatda.domain.cheer.Cheer;

public record CheerPreviewResponse(
        long storeId,
        String imageUrl,
        String storeName,
        String storeDistrict,
        String storeNeighborhood,
        String storeCategory,
        long cheerId,
        String cheerDescription,
        long memberId,
        String memberNickname
) {

    public CheerPreviewResponse(Cheer cheer, String imageUrl) {
        this(
                cheer.getStore().getId(),
                imageUrl,
                cheer.getStore().getName(),
                cheer.getStore().getAddressDistrict(),
                cheer.getStore().getAddressNeighborhood(),
                cheer.getStore().getCategory().getCategoryName(),
                cheer.getId(),
                cheer.getDescription(),
                cheer.getMember().getId(),
                cheer.getMember().getNickname()
        );
    }
}
