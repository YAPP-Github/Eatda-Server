package eatda.controller.story;

import eatda.domain.story.Story;
import java.util.List;

public record StoryInMemberResponse(
        Long id,
        List<StoryImageResponse> images,
        String storeName
) {

    public StoryInMemberResponse(Story story, List<StoryImageResponse> imageUrl) {
        this(story.getId(), imageUrl, story.getStoreName());
    }
}
