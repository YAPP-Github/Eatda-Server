package eatda.repository.store;

import eatda.domain.store.Cheer;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface CheerRepository extends Repository<Cheer, Long> {

    Cheer save(Cheer cheer);

    List<Cheer> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
