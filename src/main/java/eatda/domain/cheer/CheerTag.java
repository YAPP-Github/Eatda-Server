package eatda.domain.cheer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "cheer_tag")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheerTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cheer_id", nullable = false)
    private Cheer cheer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 63)
    private CheerTagName name;

    public CheerTag(Cheer cheer, CheerTagName name) {
        this.cheer = cheer;
        this.name = name;
    }
}
