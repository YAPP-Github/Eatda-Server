package eatda.controller.story;

import eatda.domain.story.Story;
import java.util.List;

public record StoriesDetailResponse(List<StoryDetailResponse> stories) {

    public record StoryDetailResponse(
            long storyId,
            List<String> imageUrls,
            long memberId,
            String memberNickname
    ) {

        public StoryDetailResponse(Story story, List<String> imageUrls) {
            this(
                    story.getId(),
                    imageUrls,
                    story.getMember().getId(),
                    story.getMember().getNickname()
            );
        }
    }
}
