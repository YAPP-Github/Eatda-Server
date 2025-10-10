package eatda.domain.cheer;

import eatda.domain.AuditingEntity;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "cheer")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cheer extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "cheer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CheerImage> images = new HashSet<>();

    /*
    CheerTags가 Embedded이기 때문에 BatchSize를 그대로 적용하지 못함.
    성능을 위해서는 Embedded 제거 후 직접 @OneToMany로 매핑 필요함.
    현재 데이터가 많지 않음으로 현상 유지하며 모니터링.
    추후 재설계 필요
     */
    @Embedded
    private CheerTags cheerTags;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    public Cheer(Member member, Store store, String description) {
        validateDescription(description);
        this.member = member;
        this.store = store;
        this.description = description;
        this.cheerTags = new CheerTags();

        this.isAdmin = false;
    }

    public Cheer(Member member, Store store, String description, boolean isAdmin) {
        this(member, store, description);
        this.isAdmin = isAdmin;
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_CHEER_DESCRIPTION);
        }
    }

    public void addImage(CheerImage image) {
        images.add(image);
        image.setCheer(this);
    }

    public void setCheerTags(List<CheerTagName> cheerTagNames) {
        this.cheerTags.setTags(this, cheerTagNames);
    }

    public List<CheerTagName> getCheerTagNames() {
        if (cheerTags == null) {
            return Collections.emptyList();
        }
        return cheerTags.getNames();
    }
}
