package eatda.controller.story;

import java.util.List;

public record StoriesResponse(
        List<StoryPreview> stories
) {
    public record StoryPreview(
            Long storyId,
            List<StoryImageResponse> images
    ) {
    }
}
