package eatda.repository.cheer;

import eatda.domain.ImageKey;
import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerTagName;
import eatda.domain.member.Member;
import eatda.domain.store.District;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

public interface CheerRepository extends JpaRepository<Cheer, Long> {

    @EntityGraph(attributePaths = {"store", "member", "cheerTags.values"})
    List<Cheer> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"member", "cheerTags.values"})
    List<Cheer> findAllByStoreIdOrderByCreatedAtDesc(Long storeId, Pageable pageable);

    default List<Cheer> findAllByConditions(@Nullable StoreCategory category,
                                            List<CheerTagName> cheerTagNames,
                                            List<District> districts,
                                            Pageable pageable) {
        Specification<Cheer> spec = createSpecification(category, cheerTagNames, districts);
        return findAll(spec, pageable);
    }

    private Specification<Cheer> createSpecification(@Nullable StoreCategory category,
                                                     List<CheerTagName> cheerTagNames,
                                                     List<District> districts) {
        Specification<Cheer> spec = Specification.allOf();
        if (category != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("store").get("category"), category));
        }
        if (!cheerTagNames.isEmpty()) {
            spec = spec.and(((root, query, cb) ->
                    root.get("cheerTags").get("values").get("name").in(cheerTagNames)));
        }
        if (!districts.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("store").get("district").in(districts));
        }
        return spec;
    }

    @EntityGraph(attributePaths = {"store", "member", "cheerTags.values"})
    List<Cheer> findAll(Specification<Cheer> specification, Pageable pageable);

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
