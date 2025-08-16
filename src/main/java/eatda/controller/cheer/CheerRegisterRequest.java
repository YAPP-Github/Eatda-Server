package eatda.controller.cheer;

import java.util.List;

public record CheerRegisterRequest(
        String storeName,
        String storeKakaoId,
        String description,
        List<UploadedImageDetail> images
) {
    public record UploadedImageDetail(
            String imageKey,
            long orderIndex,
            String contentType,
            long fileSize
    ) {
    }
}
