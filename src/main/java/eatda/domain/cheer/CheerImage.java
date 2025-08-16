package eatda.domain.cheer;

import eatda.domain.BaseImageEntity;
import jakarta.persistence.Entity;
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

@Table(name = "cheer_image")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheerImage extends BaseImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cheer_id", nullable = false)
    private Cheer cheer;

    public CheerImage(Cheer cheer, String imageKey, long orderIndex, String contentType, Long fileSize) {
        super(imageKey, orderIndex, contentType, fileSize);
        this.cheer = cheer;
    }

    void setCheer(Cheer cheer) {
        this.cheer = cheer;
    }
}
