package eatda.repository.story;

import eatda.domain.story.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Long> {

    Page<Story> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
