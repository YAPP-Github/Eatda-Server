package eatda.repository.cheer;

import eatda.domain.cheer.CheerImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheerImageRepository extends JpaRepository<CheerImage, Long> {

    List<CheerImage> findAllByCheer_Store_IdOrderByOrderIndexAsc(Long storeId);

    Optional<CheerImage> findFirstByCheer_Store_IdOrderByCreatedAtDesc(Long storeId);
}
