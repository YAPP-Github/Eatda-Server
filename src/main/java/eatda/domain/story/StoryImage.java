package eatda.domain.story;

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

@Table(name = "story_image")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoryImage extends BaseImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    public StoryImage(Story story, String imageKey, long orderIndex, String contentType, Long fileSize) {
        super(imageKey, orderIndex, contentType, fileSize);
        this.story = story;
    }
}
