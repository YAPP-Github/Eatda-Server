package eatda.controller.cheer;

import eatda.domain.cheer.CheerImage;

public record CheerImageResponse(
        String imageKey,
        long orderIndex,
        String contentType,
        long fileSize,
        String url
) {
    public CheerImageResponse(CheerImage cheerImage, String cdnBaseUrl) {
        this(
                cheerImage.getImageKey(),
                cheerImage.getOrderIndex(),
                cheerImage.getContentType(),
                cheerImage.getFileSize(),
                cdnBaseUrl + "/" + cheerImage.getImageKey()
        );
    }
}
