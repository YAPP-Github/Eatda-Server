package eatda.controller.story;

import eatda.domain.story.Story;
import java.util.List;

public record StoriesDetailResponse(List<StoryDetailResponse> stories) {

    public record StoryDetailResponse(
            long storyId,
            String imageUrl,
            long memberId,
            String memberNickname
    ) {

        public StoryDetailResponse(Story story, String imageUrl) {
            this(
                    story.getId(),
                    imageUrl,
                    story.getMember().getId(),
                    story.getMember().getNickname()
            );
        }
    }
}
