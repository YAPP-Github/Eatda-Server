package eatda.service.article;

import eatda.controller.article.ArticleResponse;
import eatda.controller.article.ArticlesResponse;
import eatda.repository.article.ArticleRepository;
import eatda.storage.image.ImageStorage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ImageStorage imageStorage;

    public ArticlesResponse getAllArticles(int size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        List<ArticleResponse> articles = articleRepository.findAllByOrderByCreatedAtDesc(pageRequest)
                .stream()
                .map(article -> new ArticleResponse(
                        article.getTitle(),
                        article.getSubtitle(),
                        article.getArticleUrl(),
                        imageStorage.getPreSignedUrl(article.getImageKey())
                ))
                .toList();

        return new ArticlesResponse(articles);
    }
}
