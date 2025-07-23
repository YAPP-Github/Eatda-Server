package eatda.controller.article;

import eatda.service.article.ArticleService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/api/articles")
    public ResponseEntity<ArticlesResponse> getArticles(@RequestParam(defaultValue = "3") @Min(1) @Max(50) int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(articleService.getAllArticles(size));
    }
}
