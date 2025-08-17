package eatda.controller.story;

import eatda.domain.story.Story;
import java.util.List;

public record StoriesDetailResponse(List<StoryDetailResponse> stories) {

    public record StoryDetailResponse(
            long storyId,
            List<StoryImageResponse> images,
            long memberId,
            String memberNickname
    ) {
        public StoryDetailResponse(Story story, List<StoryImageResponse> images) {
            this(
                    story.getId(),
                    images,
                    story.getMember().getId(),
                    story.getMember().getNickname());
        }
    }
}
