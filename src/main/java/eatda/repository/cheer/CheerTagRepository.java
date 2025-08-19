package eatda.repository.cheer;

import eatda.domain.cheer.CheerTag;
import eatda.domain.store.Store;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheerTagRepository extends JpaRepository<CheerTag, Long> {

    List<CheerTag> findAllByCheerStore(Store storeId);
}
