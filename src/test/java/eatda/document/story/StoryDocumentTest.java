package eatda.document.story;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;

import eatda.controller.story.StoriesResponse;
import eatda.controller.story.StoryRegisterRequest;
import eatda.controller.story.StoryRegisterResponse;
import eatda.controller.story.StoryResponse;
import eatda.document.BaseDocumentTest;
import eatda.document.RestDocsRequest;
import eatda.document.RestDocsResponse;
import eatda.document.Tag;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.util.ImageUtils;
import eatda.util.MappingUtils;
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
                ).multipartField(
                        partWithName("image").description("스토리 이미지 (필수)"),
                        partWithName("request").description("스토리 등록 요청 정보")
                ).requestBodyField("request",
                        fieldWithPath("query").description("스토리 검색 쿼리"),
                        fieldWithPath("storeKakaoId").description("가게의 카카오 ID"),
                        fieldWithPath("description").description("스토리 내용 (필수)")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("storyId").description("등록된 스토리의 ID")
                );

        @Test
        void 스토리_등록_성공() {
            StoryRegisterRequest request = new StoryRegisterRequest("농민백암순대", "123", "여기 진짜 맛있어요!");
            StoryRegisterResponse response = new StoryRegisterResponse(1L);
            doReturn(response).when(storyService).registerStory(any(), any(), any());

            var document = document("story/register", 201)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .contentType("multipart/form-data")
                    .multiPart("request", "request.json", MappingUtils.toJsonBytes(request), "application/json")
                    .multiPart("image", ImageUtils.getTestImage(), "image/png")
                    .when().post("/api/stories")
                    .then().statusCode(201);
        }

        @Test
        void 스토리_등록_실패_이미지_형식_오류() {
            StoryRegisterRequest request = new StoryRegisterRequest("농민백암순대", "123", "여기 진짜 맛있어요!");
            byte[] invalidImage = "not an image".getBytes(StandardCharsets.UTF_8);
            doThrow(new BusinessException(BusinessErrorCode.INVALID_IMAGE_TYPE))
                    .when(storyService)
                    .registerStory(any(), any(), any());

            var document = document("story/register", BusinessErrorCode.INVALID_IMAGE_TYPE)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .contentType("multipart/form-data")
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .multiPart("request", "request.json", MappingUtils.toJsonBytes(request), "application/json")
                    .multiPart("image", "image.txt", invalidImage, "text/plain")
                    .when().post("/api/stories")
                    .then().statusCode(BusinessErrorCode.INVALID_IMAGE_TYPE.getStatus().value());
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
            int size = 5;
            StoriesResponse response = new StoriesResponse(List.of(
                    new StoriesResponse.StoryPreview(1L, "https://dummy-s3.com/story1.png"),
                    new StoriesResponse.StoryPreview(2L, "https://dummy-s3.com/story2.png")
            ));
            doReturn(response).when(storyService).getPagedStoryPreviews(size);

            var document = document("story/get-stories", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .queryParam("size", 5)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .when().get("/api/stories")
                    .then().statusCode(200);
        }
    }

    @Nested
    class GetStory {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORY_API)
                .summary("스토리 상세 조회")
                .description("스토리 ID를 기반으로 상세 정보를 조회합니다.");

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("storeKakaoId").description("가게의 카카오 ID"),
                        fieldWithPath("category").description("가게 카테고리"),
                        fieldWithPath("storeName").description("가게 이름"),
                        fieldWithPath("storeDistrict").description("가게 주소의 구"),
                        fieldWithPath("storeNeighborhood").description("가게 주소의 동"),
                        fieldWithPath("description").description("스토리 내용"),
                        fieldWithPath("imageUrl").description("스토리 이미지 URL")
                );

        @Test
        void 스토리_상세_조회_성공() {
            long storyId = 1L;
            StoryResponse response = new StoryResponse(
                    "123456",
                    "한식",
                    "진또곱창집",
                    "성동구",
                    "성수동",
                    "곱창은 여기",
                    "https://s3.bucket.com/story1.jpg"
            );
            doReturn(response).when(storyService).getStory(storyId);

            RestDocumentationFilter document = document("story/get-story", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .pathParam("storyId", storyId)
                    .when().get("/api/stories/{storyId}")
                    .then().statusCode(200);
        }

        @Test
        void 스토리_상세_조회_실패_존재하지_않는_스토리() {
            long nonexistentId = 999L;

            doThrow(new BusinessException(BusinessErrorCode.STORY_NOT_FOUND))
                    .when(storyService).getStory(nonexistentId);

            RestDocumentationFilter document = document("story/get-story", BusinessErrorCode.STORY_NOT_FOUND)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            Response response = given(document)
                    .pathParam("storyId", nonexistentId)
                    .when()
                    .get("/api/stories/{storyId}");

            response.then()
                    .statusCode(BusinessErrorCode.STORY_NOT_FOUND.getStatus().value())
                    .body("errorCode", equalTo(BusinessErrorCode.STORY_NOT_FOUND.getCode()))
                    .body("message", equalTo(BusinessErrorCode.STORY_NOT_FOUND.getMessage()));
        }
    }
}
