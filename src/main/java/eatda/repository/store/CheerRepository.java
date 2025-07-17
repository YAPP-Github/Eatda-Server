package eatda.repository.store;

import eatda.domain.store.Cheer;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheerRepository extends JpaRepository<Cheer, Long> {

    List<Cheer> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
