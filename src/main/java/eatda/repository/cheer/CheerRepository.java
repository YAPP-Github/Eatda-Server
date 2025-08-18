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

    @EntityGraph(attributePaths = {"store", "member", "cheerTags.values", "images"})
    List<Cheer> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"member", "cheerTags.values", "images"})
    List<Cheer> findAllByStoreIdOrderByCreatedAtDesc(Long storeId, Pageable pageable);

    int countByMember(Member member);

    int countByStore(Store store);

    boolean existsByMemberAndStoreKakaoId(Member member, String storeKakaoId);
}
