package eatda.domain.article;

import eatda.domain.AuditingEntity;
import eatda.domain.ImageKey;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "article")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subtitle;

    @Column(name = "article_url", nullable = false, length = 511)
    private String articleUrl;

    @NotNull
    @Embedded
    private ImageKey imageKey;

    public Article(String title, String subtitle, String articleUrl, ImageKey imageKey) {
        this.title = title;
        this.subtitle = subtitle;
        this.articleUrl = articleUrl;
        this.imageKey = imageKey;
    }
}
