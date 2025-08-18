package eatda.controller.story;

import eatda.domain.story.Story;

public record StoryInMemberResponse(
        Long id,
        String imageUrl,
        String storeName
) {

    public StoryInMemberResponse(Story story, String imageUrl) {
        this(story.getId(), imageUrl, story.getStoreName());
    }
}
