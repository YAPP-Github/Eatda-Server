package eatda.repository.cheer;

import eatda.domain.ImageKey;
import eatda.domain.cheer.Cheer;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CheerRepository extends JpaRepository<Cheer, Long> {

    @EntityGraph(attributePaths = {"store", "member"})
    List<Cheer> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"member"})
    List<Cheer> findAllByStoreOrderByCreatedAtDesc(Store store, Pageable pageable);

    @Query("""
            SELECT c.imageKey FROM Cheer c
                WHERE c.store = :store AND c.imageKey IS NOT NULL
                ORDER BY c.createdAt DESC
                LIMIT 1""")
    Optional<ImageKey> findRecentImageKey(Store store);

    @Query("""
            SELECT c.imageKey FROM Cheer c
                WHERE c.store = :store AND c.imageKey IS NOT NULL
                ORDER BY c.createdAt DESC""")
    List<ImageKey> findAllImageKey(Store store);

    int countByMember(Member member);

    int countByStore(Store store);

    boolean existsByMemberAndStoreKakaoId(Member member, String storeKakaoId);
}
