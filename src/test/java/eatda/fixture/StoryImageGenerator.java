package eatda.fixture;

import eatda.domain.story.Story;
import eatda.domain.story.StoryImage;
import eatda.repository.story.StoryImageRepository;
import org.springframework.stereotype.Component;

@Component
public class StoryImageGenerator {

    private final StoryImageRepository storyImageRepository;

    public StoryImageGenerator(StoryImageRepository storyImageRepository) {
        this.storyImageRepository = storyImageRepository;
    }

    public StoryImage generate(Story story) {
        return generate(story, "dummy/story-image.png", 1L, "image/png", 1000L);
    }

    public StoryImage generate(Story story, String imageKey, long orderIndex) {
        return generate(story, imageKey, orderIndex, "image/png", 1000L);
    }

    public StoryImage generate(Story story, String imageKey, long orderIndex, String contentType, long fileSize) {
        StoryImage storyImage = new StoryImage(
                story,
                imageKey,
                orderIndex,
                contentType,
                fileSize
        );
        story.getImages().add(storyImage);
        return storyImageRepository.save(storyImage);
    }
}
