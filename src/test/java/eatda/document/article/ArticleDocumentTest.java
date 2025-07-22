package eatda.document.article;

import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

import eatda.controller.article.ArticleResponse;
import eatda.controller.article.ArticlesResponse;
import eatda.document.BaseDocumentTest;
import eatda.document.RestDocsRequest;
import eatda.document.RestDocsResponse;
import eatda.document.Tag;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class ArticleDocumentTest extends BaseDocumentTest {

    @Nested
    class GetArticles {

        RestDocsRequest requestDocument = request()
                .tag(Tag.ARTICLE_API)
                .summary("가게의 담긴 이야기")
                .description("게시글을 최신순으로 페이지네이션하여 조회합니다.")
                .queryParameter(parameterWithName("size").description("페이지당 조회할 아티클 개수 (default = 3)"));

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("articles").description("게시글 응답 리스트"),
                        fieldWithPath("articles[].title").description("게시글 제목"),
                        fieldWithPath("articles[].subtitle").description("게시글 소제목"),
                        fieldWithPath("articles[].articleUrl").description("게시글 링크 URL"),
                        fieldWithPath("articles[].imageUrl").description("게시글 이미지 URL")
                );

        @Test
        void 가게의_담긴_이야기_목록_조회_성공() {
            ArticlesResponse mockResponse = new ArticlesResponse(List.of(
                    new ArticleResponse(
                            "국밥의 모든 것",
                            "뜨끈한 국물의 세계",
                            "https://eatda.com/article/1",
                            "https://s3.bucket.com/article/1.jpg"
                    ),
                    new ArticleResponse(
                            "순대국의 진실",
                            "돼지부속의 미학",
                            "https://eatda.com/article/2",
                            "https://s3.bucket.com/article/2.jpg"
                    )
            ));

            doReturn(mockResponse)
                    .when(articleService)
                    .getAllArticles(3);

            RestDocumentationFilter document = document("article/get-articles", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            Response response = given(document)
                    .queryParam("size", 3)
                    .when()
                    .get("/api/articles");

            response.then()
                    .statusCode(200);
        }
    }
}
