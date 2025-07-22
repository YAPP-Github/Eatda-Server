package eatda.fixture;

import eatda.controller.article.ArticleResponse;
import eatda.domain.article.Article;
import eatda.repository.article.ArticleRepository;
import org.springframework.stereotype.Component;

@Component
public class ArticleGenerator {

    private static final String DEFAULT_TITLE = "기본 제목";
    private static final String DEFAULT_SUBTITLE = "기본 소제목";
    private static final String DEFAULT_URL = "https://eatda.com/article/default";
    private static final String DEFAULT_IMAGE_KEY = "article/default-image.jpg";

    private final ArticleRepository articleRepository;

    public ArticleGenerator(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Article generate() {
        return generate(DEFAULT_TITLE);
    }

    public Article generate(String title) {
        return generate(title, DEFAULT_SUBTITLE);
    }

    public Article generate(String title, String subtitle) {
        return generate(title, subtitle, DEFAULT_URL);
    }

    public Article generate(String title, String subtitle, String articleUrl) {
        return generate(title, subtitle, articleUrl, DEFAULT_IMAGE_KEY);
    }

    public Article generate(String title, String subtitle, String articleUrl, String imageKey) {
        Article article = new Article(title, subtitle, articleUrl, imageKey);
        return articleRepository.save(article);
    }

    public ArticleResponse toResponse(Article article) {
        return new ArticleResponse(
                article.getTitle(),
                article.getSubtitle(),
                article.getArticleUrl(),
                "https://s3.bucket.com/" + article.getImageKey()
        );
    }
}
