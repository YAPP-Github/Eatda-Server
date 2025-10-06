package eatda.repository.cheer;

import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerTagName;
import eatda.domain.member.Member;
import eatda.domain.store.District;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

public interface CheerRepository extends JpaRepository<Cheer, Long> {

    Page<Cheer> findAllByStoreOrderByCreatedAtDesc(Store store, PageRequest pageRequest);

    default Page<Cheer> findAllByConditions(@Nullable StoreCategory category,
                                            List<CheerTagName> cheerTagNames,
                                            List<District> districts, Pageable pageable) {
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
            spec = spec.and(((root, query, cb) -> {
                if (query != null) {
                    query.distinct(true);
                }
                return root.join("cheerTags").join("values", JoinType.LEFT)
                        .get("name").in(cheerTagNames);
            }));
        }
        if (!districts.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("store").get("district").in(districts));
        }
        return spec;
    }

    Page<Cheer> findAll(Specification<Cheer> specification, Pageable pageable);

    int countByMember(Member member);

    int countByStore(Store store);

    boolean existsByMemberAndStoreKakaoId(Member member, String storeKakaoId);
}
