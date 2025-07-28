package eatda.controller.article;

import static org.assertj.core.api.Assertions.assertThat;

import eatda.controller.BaseControllerTest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ArticleControllerTest extends BaseControllerTest {

    @Nested
    class GetArticles {

        @Test
        void 가게의_담긴_이야기_목록을_조회할_수_있다() {
            LocalDateTime startAt = LocalDateTime.of(2023, 10, 1, 0, 0);
            articleGenerator.generate("국밥의 모든 것", startAt);
            articleGenerator.generate("순대국의 진실", startAt.plusHours(1));

            ArticlesResponse response = given()
                    .queryParam("size", 3)
                    .when()
                    .get("/api/articles")
                    .then().statusCode(200)
                    .extract().as(ArticlesResponse.class);

            assertThat(response.articles()).hasSize(2);
            assertThat(response.articles().getFirst().title()).isEqualTo("순대국의 진실");
        }
    }
}
