package eatda.controller.cheer;

import eatda.domain.cheer.CheerImage;

public record CheerImageResponse(
        String imageKey,
        long orderIndex,
        String contentType,
        long fileSize
) {
    public CheerImageResponse(CheerImage cheerImage) {
        this(
                cheerImage.getImageKey(),
                cheerImage.getOrderIndex(),
                cheerImage.getContentType(),
                cheerImage.getFileSize()
        );
    }
}
