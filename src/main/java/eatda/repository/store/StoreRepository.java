package eatda.repository.store;

import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoreRepository extends JpaRepository<Store, Long> {

    @Override
    default Store getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.STORE_NOT_FOUND));
    }

    Optional<Store> findByKakaoId(String kakaoId);

    @EntityGraph(attributePaths = {"cheers"})
    List<Store> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"cheers"})
    List<Store> findAllByCategoryOrderByCreatedAtDesc(StoreCategory category, Pageable pageable);

    @Query("""
            SELECT s FROM Store s
                JOIN Cheer c ON s.id = c.store.id
                WHERE c.member.id = :memberId
                ORDER BY c.createdAt DESC
            """)
    List<Store> findAllByCheeredMemberId(long memberId);
}
