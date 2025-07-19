package eatda.repository.store;

import eatda.domain.store.Store;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface StoreRepository extends Repository<Store, Long> {

    Store save(Store store);

    Optional<Store> findByKakaoId(String kakaoId);

    List<Store> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
