package eatda.controller.story;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import eatda.controller.BaseControllerTest;
import eatda.service.common.ImageDomain;
import io.restassured.response.Response;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StoryControllerTest extends BaseControllerTest {

    @BeforeEach
    void setUpMock() {
        doNothing()
                .when(storyService)
                .registerStory(any(), any(), any());
    }

    @Nested
    class SearchStores {

        @Test
        void 스토리를_등록할_수_있다() {
            String requestJson = """
                        {
                          "query": "농민백암순대",
                          "storeKakaoId": "123",
                          "description": "여기 진짜 맛있어요!"
                        }
                    """;

            byte[] imageBytes = "dummy image content".getBytes(StandardCharsets.UTF_8);

            Response response = given()
                    .contentType("multipart/form-data")
                    .header("Authorization", accessToken())
                    .multiPart("request", "request.json", requestJson.getBytes(StandardCharsets.UTF_8), "application/json")
                    .multiPart("image", "image.png", imageBytes, "image/png")
                    .when()
                    .post("/api/stories");

            response.then().statusCode(201);
        }
    }
}
