package eatda.domain.cheer;

import eatda.domain.AuditingEntity;
import eatda.domain.ImageKey;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Collections;
import java.util.List;
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

    @Embedded
    private ImageKey imageKey;

    @Embedded
    private CheerTags cheerTags;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    public Cheer(Member member, Store store, String description, ImageKey imageKey) {
        validateDescription(description);
        this.member = member;
        this.store = store;
        this.description = description;
        this.imageKey = imageKey;
        this.cheerTags = new CheerTags();

        this.isAdmin = false;
    }

    public Cheer(Member member, Store store, String description, ImageKey imageKey, boolean isAdmin) {
        this(member, store, description, imageKey);
        this.isAdmin = isAdmin;
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_CHEER_DESCRIPTION);
        }
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
