package eatda.repository.story;

import eatda.domain.story.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Long> {
    @EntityGraph(attributePaths = "images")
    Page<Story> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Story> findAllByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    @EntityGraph(attributePaths = {"member", "images"})
    Page<Story> findAllByStoreKakaoIdOrderByCreatedAtDesc(String storeKakaoId, Pageable pageable);
}
