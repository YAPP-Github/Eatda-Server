package eatda.domain.article;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "article")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subtitle;

    @Column(name = "article_url", nullable = false, length = 511)
    private String articleUrl;

    @Column(name = "image_key", nullable = false, length = 511)
    private String imageKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Article(String title, String subtitle, String articleUrl, String imageKey) {
        this.title = title;
        this.subtitle = subtitle;
        this.articleUrl = articleUrl;
        this.imageKey = imageKey;
        this.createdAt = LocalDateTime.now();
    }
}
