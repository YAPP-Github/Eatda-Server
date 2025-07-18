package eatda.repository.store;

import eatda.domain.store.Store;
import org.springframework.data.repository.Repository;

public interface StoreRepository extends Repository<Store, Long> {

    Store save(Store store);
}
