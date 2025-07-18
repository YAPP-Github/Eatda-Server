package eatda.repository.store;

import eatda.domain.store.Store;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface StoreRepository extends Repository<Store, Long> {

    Store save(Store store);

    List<Store> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
