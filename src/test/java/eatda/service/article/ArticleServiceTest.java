package eatda.service.article;

import static org.assertj.core.api.Assertions.assertThat;

import eatda.controller.article.ArticleResponse;
import eatda.service.BaseServiceTest;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ArticleServiceTest extends BaseServiceTest {

    @Autowired
    private ArticleService articleService;

    @Nested
    class GetAllArticles {

        @Test
        void 가게의_담긴_이야기를_최신순으로_조회할_수_있다() {
            LongStream.rangeClosed(1, 5)
                    .forEach(i -> articleGenerator.generate("아티클 제목 " + i));

            var response = articleService.getAllArticles(3);

            assertThat(response.articles()).hasSize(3);
            List<String> titles = response.articles().stream()
                    .map(ArticleResponse::title)
                    .toList();
            assertThat(titles).containsExactly(
                    "아티클 제목 5",
                    "아티클 제목 4",
                    "아티클 제목 3"
            );
        }
    }
}
