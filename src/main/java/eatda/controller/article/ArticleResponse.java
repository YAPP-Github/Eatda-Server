package eatda.controller.article;

public record ArticleResponse(
        String title,
        String subtitle,
        String articleUrl,
        String imageUrl
) {
}
