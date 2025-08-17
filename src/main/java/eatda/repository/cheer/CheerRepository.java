package eatda.repository.cheer;

import eatda.domain.cheer.Cheer;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheerRepository extends JpaRepository<Cheer, Long> {
    @EntityGraph(attributePaths = "images")
    List<Cheer> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = "images")
    List<Cheer> findAllByStoreOrderByCreatedAtDesc(Store store, Pageable pageable);

    int countByMember(Member member);

    boolean existsByMemberAndStoreKakaoId(Member member, String storeKakaoId);
}
