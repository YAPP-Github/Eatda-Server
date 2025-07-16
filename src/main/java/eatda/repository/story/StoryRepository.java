package eatda.repository.story;

import eatda.domain.story.Story;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface StoryRepository extends Repository<Story, Long> {

    Story save(Story story);

    Optional<Story> findById(Long id);

    default Story getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.INVALID_STORE_ID));
    }
}
