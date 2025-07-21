package eatda.service.article;

import eatda.controller.article.ArticleResponse;
import eatda.controller.article.ArticlesResponse;
import eatda.repository.article.ArticleRepository;
import eatda.service.common.ImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ImageService imageService;

    public ArticlesResponse getAllArticles(int size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        List<ArticleResponse> articles = articleRepository.findAllByOrderByCreatedAtDesc(pageRequest)
                .stream()
                .map(article -> new ArticleResponse(
                        article.getId(),
                        article.getTitle(),
                        article.getSubtitle(),
                        article.getArticleUrl(),
                        imageService.getPresignedUrl(article.getImageKey()),
                        article.getCreatedAt()
                ))
                .toList();

        return new ArticlesResponse(articles);
    }
}
