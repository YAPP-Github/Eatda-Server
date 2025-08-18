package eatda.repository.cheer;

import eatda.domain.cheer.Cheer;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheerRepository extends JpaRepository<Cheer, Long> {

    @EntityGraph(attributePaths = {"store", "member", "cheerTags.values", "images"})
    List<Cheer> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Cheer> findAllByStoreOrderByCreatedAtDesc(Store store, PageRequest of);

    int countByMember(Member member);

    int countByStore(Store store);

    boolean existsByMemberAndStoreKakaoId(Member member, String storeKakaoId);
}
