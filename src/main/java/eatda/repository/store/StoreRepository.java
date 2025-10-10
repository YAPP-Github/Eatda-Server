package eatda.repository.store;

import eatda.domain.cheer.CheerTagName;
import eatda.domain.store.District;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

public interface StoreRepository extends JpaRepository<Store, Long> {

    @Override
    default Store getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.STORE_NOT_FOUND));
    }

    Optional<Store> findByKakaoId(String kakaoId);

    @Query("""
            SELECT s FROM Store s
                JOIN Cheer c ON s.id = c.store.id
                WHERE c.member.id = :memberId
                ORDER BY c.createdAt DESC
            """)
    List<Store> findAllByCheeredMemberId(long memberId);

    default Page<Store> findAllByConditions(@Nullable StoreCategory category,
                                            List<CheerTagName> cheerTagNames,
                                            List<District> districts,
                                            Pageable pageable) {
        Specification<Store> spec = createSpecification(category, cheerTagNames, districts);
        return findAll(spec, pageable);
    }

    Page<Store> findAll(Specification<Store> spec, Pageable pageable);

    private Specification<Store> createSpecification(@Nullable StoreCategory category,
                                                     List<CheerTagName> cheerTagNames,
                                                     List<District> districts) {
        Specification<Store> spec = Specification.allOf();
        if (category != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        if (!cheerTagNames.isEmpty()) {
            spec = spec.and(((root, query, cb) -> {
                if (query != null) {
                    query.distinct(true);
                }
                return root.join("cheers").join("cheerTags").join("values").get("name").in(cheerTagNames);
            }));
        }
        if (!districts.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("district").in(districts));
        }
        return spec;
    }
}
