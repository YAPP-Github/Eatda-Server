package eatda.document.story;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import eatda.controller.story.StoriesResponse;
import eatda.document.BaseDocumentTest;
import eatda.document.RestDocsRequest;
import eatda.document.RestDocsResponse;
import eatda.document.Tag;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.exception.EtcErrorCode;
import eatda.service.common.ImageDomain;
import io.restassured.response.Response;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class StoryDocumentTest extends BaseDocumentTest {

    @Nested
    class RegisterStory {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORY_API)
                .summary("스토리 등록")
                .description("스토리와 이미지를 multipart/form-data로 등록합니다.")
                .requestHeader(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                );

        RestDocsResponse responseDocument = response();

        @Test
        void 스토리_등록_성공() {
            doReturn("https://dummy-s3.com/story.png")
                    .when(imageService)
                    .upload(any(), org.mockito.ArgumentMatchers.eq(ImageDomain.STORY));

            doNothing().when(storyService)
                    .registerStory(any(), any(), any());

            String requestJson = """
                    {
                      "query": "농민백암순대",
                      "storeKakaoId": "123",
                      "description": "여기 진짜 맛있어요!"
                    }
                    """;

            byte[] imageBytes = "dummy image content".getBytes(StandardCharsets.UTF_8);

            RestDocumentationFilter document = document("story/register", 201)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            Response response = given(document)
                    .contentType("multipart/form-data")
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .multiPart("request", "request.json", requestJson.getBytes(StandardCharsets.UTF_8), "application/json")
                    .multiPart("image", "image.png", imageBytes, "image/png")
                    .when().post("/api/stories");

            response.then().statusCode(201);
        }

        @Test
        void 스토리_등록_실패_필수값_누락() {
            String invalidJson = """
                    {
                      "query": "농민백암순대",
                      "storeKakaoId": "123"
                    }
                    """;

            byte[] imageBytes = "dummy image content".getBytes(StandardCharsets.UTF_8);

            doThrow(new BusinessException(BusinessErrorCode.INVALID_STORY_DESCRIPTION))
                    .when(storyService)
                    .registerStory(any(), any(), any());

            var document = document("story/register", EtcErrorCode.CLIENT_REQUEST_ERROR)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType("multipart/form-data")
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .multiPart("request", "request.json", invalidJson.getBytes(StandardCharsets.UTF_8), "application/json")
                    .multiPart("image", "image.png", imageBytes, "image/png")
                    .when().post("/api/stories")
                    .then().statusCode(EtcErrorCode.CLIENT_REQUEST_ERROR.getStatus().value());
        }

        @Test
        void 스토리_등록_실패_이미지_형식_오류() {
            String requestJson = """
                    {
                      "query": "농민백암순대",
                      "storeKakaoId": "123",
                      "description": "여기 진짜 맛있어요!"
                    }
                    """;

            byte[] invalidImage = "not an image".getBytes(StandardCharsets.UTF_8);

            doThrow(new BusinessException(BusinessErrorCode.INVALID_IMAGE_TYPE))
                    .when(storyService)
                    .registerStory(any(), any(), any());

            var document = document("story/register", BusinessErrorCode.INVALID_IMAGE_TYPE)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            Response response = given(document)
                    .contentType("multipart/form-data")
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .multiPart("request", "request.json", requestJson.getBytes(StandardCharsets.UTF_8), "application/json")
                    .multiPart("image", "image.txt", invalidImage, "text/plain")
                    .when().post("/api/stories");

            response.then().statusCode(BusinessErrorCode.INVALID_IMAGE_TYPE.getStatus().value());
        }
    }

    @Nested
    class GetStories {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORY_API)
                .summary("스토리 목록 조회")
                .description("스토리 목록을 페이지네이션하여 조회합니다.");

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("stories").description("스토리 프리뷰 리스트"),
                        fieldWithPath("stories[].storyId").description("스토리 ID"),
                        fieldWithPath("stories[].imageUrl").description("스토리 이미지 URL")
                );

        @Test
        void 스토리_목록_조회_성공() {
            StoriesResponse mockResponse = new StoriesResponse(List.of(
                    new StoriesResponse.StoryPreview(1L, "https://dummy-s3.com/story1.png"),
                    new StoriesResponse.StoryPreview(2L, "https://dummy-s3.com/story2.png")
            ));

            doReturn(mockResponse)
                    .when(storyService)
                    .getPagedStoryPreviews();

            RestDocumentationFilter document = document("story/get-stories", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            Response response = given(document)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .when().get("/api/stories");

            response.then().statusCode(200);
        }
    }
}
