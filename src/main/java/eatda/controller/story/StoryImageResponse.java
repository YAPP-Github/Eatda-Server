package eatda.controller.story;

import eatda.domain.story.StoryImage;

public record StoryImageResponse(
        String imageKey,
        long orderIndex,
        String contentType,
        long fileSize,
        String url
) {
    public StoryImageResponse(StoryImage storyImage, String cdnBaseUrl) {
        this(
                storyImage.getImageKey(),
                storyImage.getOrderIndex(),
                storyImage.getContentType(),
                storyImage.getFileSize(),
                cdnBaseUrl + "/" + storyImage.getImageKey()
        );
    }
}
