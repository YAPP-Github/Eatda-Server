package eatda.controller.story;

import java.util.List;

public record StoryRegisterRequest(
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
