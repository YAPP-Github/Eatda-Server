package eatda.controller.story;

import eatda.domain.story.StoryImage;

public record StoryImageResponse(
        String imageKey,
        long orderIndex,
        String contentType,
        long fileSize
) {
    public StoryImageResponse(StoryImage storyImage) {
        this(
                storyImage.getImageKey(),
                storyImage.getOrderIndex(),
                storyImage.getContentType(),
                storyImage.getFileSize()
        );
    }
}
