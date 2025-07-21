package eatda.controller.article;

import java.time.LocalDateTime;

public record ArticleResponse(
        Long id,
        String title,
        String subtitle,
        String articleUrl,
        String imageUrl,
        LocalDateTime createdAt
) {
}
