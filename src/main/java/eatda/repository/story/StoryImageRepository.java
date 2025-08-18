package eatda.repository.story;

import eatda.domain.story.StoryImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryImageRepository extends JpaRepository<StoryImage, Long> {

    List<StoryImage> findAllByStory_IdOrderByOrderIndexAsc(Long storyId);

    Optional<StoryImage> findFirstByStory_IdOrderByCreatedAtDesc(Long storyId);
}
