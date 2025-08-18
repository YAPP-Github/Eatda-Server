package eatda.document.story;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;

import eatda.controller.story.StoriesDetailResponse;
import eatda.controller.story.StoriesDetailResponse.StoryDetailResponse;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class StoryDocumentTest extends BaseDocumentTest {

    @Nested
    class RegisterStory {


        RestDocsRequest requestDocument = request()
                .tag(Tag.STORY_API)
                .summary("스토리 등록")
                .requestHeader(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ).requestBodyField(
                        fieldWithPath("storeName").description("가게 이름"),
                        fieldWithPath("storeKakaoId").description("가게의 카카오 ID"),
                        fieldWithPath("description").description("스토리 내용 (선택)").optional(),
                        fieldWithPath("images").type(ARRAY).description("업로드된 이미지 리스트").optional(),
                        fieldWithPath("images[].imageKey").type(STRING).description("S3 임시 업로드 키"),
                        fieldWithPath("images[].orderIndex").type(NUMBER).description("노출 순서"),
                        fieldWithPath("images[].contentType").type(STRING).description("이미지 MIME 타입"),
                        fieldWithPath("images[].fileSize").type(NUMBER).description("파일 크기 (byte)")
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("storyId").description("등록된 스토리의 ID")
                );

        @Test
        void 스토리_등록_성공() {
            StoryRegisterRequest request = new StoryRegisterRequest("농민백암순대", "123", "여기 진짜 맛있어요!", new ArrayList<>());
            StoryRegisterResponse response = new StoryRegisterResponse(1L);
            doReturn(response).when(storyService).registerStory(any(), any(), any(), anyLong());

            var document = document("story/register", 201)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/api/stories")
                    .then().statusCode(201);
        }

        @Test
        void 스토리_등록_실패_이미지_형식_오류() {
            StoryRegisterRequest request = new StoryRegisterRequest("농민백암순대", "123", "여기 진짜 맛있어요!", new ArrayList<>());
            doThrow(new BusinessException(BusinessErrorCode.INVALID_IMAGE_TYPE))
                    .when(storyService).registerStory(any(), any(), any(), anyLong());

            var document = document("story/register", BusinessErrorCode.INVALID_IMAGE_TYPE)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/api/stories")
                    .then().statusCode(BusinessErrorCode.INVALID_IMAGE_TYPE.getStatus().value());
        }
    }

    @Nested
    class GetStories {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORY_API)
                .summary("스토리 목록 조회")
                .description("스토리 목록을 페이지네이션하여 조회합니다.")
                .queryParameter(
                        parameterWithName("size").description("스토리 개수 (기본값: 5) (최소값: 1, 최대값: 50)").optional()
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("stories").type(JsonFieldType.ARRAY).description("스토리 리스트"),
                        fieldWithPath("stories[].storyId").type(JsonFieldType.NUMBER).description("스토리 ID"),
                        fieldWithPath("stories[].images").type(JsonFieldType.ARRAY).description("스토리 이미지 리스트"),
                        fieldWithPath("stories[].images[].imageKey").type(JsonFieldType.STRING).description("이미지 S3 키"),
                        fieldWithPath("stories[].images[].orderIndex").type(JsonFieldType.NUMBER).description("이미지 노출 순서"),
                        fieldWithPath("stories[].images[].contentType").type(JsonFieldType.STRING).description("이미지 MIME 타입"),
                        fieldWithPath("stories[].images[].fileSize").type(JsonFieldType.NUMBER).description("이미지 파일 크기 (byte)"),
                        fieldWithPath("stories[].images[].url").type(JsonFieldType.STRING).description("이미지 CDN URL")
                );

        @Test
        void 스토리_목록_조회_성공() {
            int size = 5;
            StoriesResponse response = new StoriesResponse(List.of(
                    new StoriesResponse.StoryPreview(
                            1L,
                            List.of(new StoryImageResponse("1.png", 0, "image/png", 12345L, "https://cdn.test/1.png"))
                    ),
                    new StoriesResponse.StoryPreview(
                            2L,
                            List.of(new StoryImageResponse("2.png", 1, "image/png", 67890L, "https://cdn.test/2.png"))
                    )
            ));
            doReturn(response).when(storyService).getPagedStoryPreviews(size);

            var document = document("story/get-stories", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .queryParam("size", size)
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
                        fieldWithPath("storeId").type(NUMBER).description("가게 ID (nullable)").optional(),
                        fieldWithPath("storeKakaoId").type(STRING).description("가게의 카카오 ID"),
                        fieldWithPath("category").type(STRING).description("가게 카테고리"),
                        fieldWithPath("storeName").type(STRING).description("가게 이름"),
                        fieldWithPath("storeDistrict").type(STRING).description("가게 주소의 구"),
                        fieldWithPath("storeNeighborhood").type(STRING).description("가게 주소의 동"),
                        fieldWithPath("description").type(STRING).description("스토리 내용"),
                        fieldWithPath("images").type(ARRAY).description("스토리 이미지 리스트"),
                        fieldWithPath("images[].imageKey").type(STRING).description("이미지 S3 키"),
                        fieldWithPath("images[].orderIndex").type(NUMBER).description("노출 순서"),
                        fieldWithPath("images[].contentType").type(STRING).description("이미지 MIME 타입"),
                        fieldWithPath("images[].fileSize").type(NUMBER).description("이미지 파일 크기 (byte)"),
                        fieldWithPath("images[].url").type(STRING).description("스토리 이미지 CDN URL"),
                        fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
                        fieldWithPath("memberNickname").type(STRING).description("회원 닉네임")
                );

        @Test
        void 스토리_상세_조회_성공() {
            long storyId = 1L;
            StoryResponse response = new StoryResponse(
                    null,
                    "123456",
                    "한식",
                    "진또곱창집",
                    "성동구",
                    "성수동",
                    "곱창은 여기",
                    List.of(new StoryImageResponse("1.png", 0, "image/png", 12345, "https://cdn.test/1.png")),
                    1L,
                    "커찬"
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

    @Nested
    class GetStoriesByMemberId {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORY_API)
                .summary("회원 ID로 스토리 목록 조회")
                .requestHeader(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                )
                .queryParameter(
                        parameterWithName("page").description("페이지 번호 (기본값: 0) (최소값: 0)").optional(),
                        parameterWithName("size").description("스토리 개수 (기본값: 5) (최소값: 1, 최대값: 50)").optional()
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("stories").type(ARRAY).description("스토리 리스트"),
                        fieldWithPath("stories[].id").type(NUMBER).description("스토리 ID"),
                        fieldWithPath("stories[].imageUrl").type(STRING).description("스토리 이미지 URL"),
                        fieldWithPath("stories[].storeName").type(STRING).description("가게 이름")
                );

        @Test
        void 회원_ID로_스토리_목록_조회_성공() {
            int page = 0;
            int size = 5;
            StoriesInMemberResponse response = new StoriesInMemberResponse(List.of(
                    new StoryInMemberResponse(1L, "https://dummy-s3.com/story1.png", "백암순대"),
                    new StoryInMemberResponse(2L, "https://dummy-s3.com/story2.png", "맥도날드")
            ));
            doReturn(response).when(storyService).getPagedStoryByMemberId(anyLong(), eq(page), eq(size));

            var document = document("story/get-stories-by-member-id", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .when().get("/api/stories/member")
                    .then().statusCode(200);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {
                "UNAUTHORIZED_MEMBER",
                "EXPIRED_TOKEN",
                "INVALID_MEMBER_ID"})
        @ParameterizedTest
        void 회원_ID로_스토리_목록_조회_실패(BusinessErrorCode errorCode) {
            int page = 0;
            int size = 5;
            doThrow(new BusinessException(errorCode))
                    .when(storyService).getPagedStoryByMemberId(anyLong(), eq(page), eq(size));

            var document = document("story/get-stories-by-member-id", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .when().get("/api/stories/member")
                    .then().statusCode(errorCode.getStatus().value());
        }
    }

    @Nested
    class GetStoriesByKakaoId {

        RestDocsRequest requestDocument = request()
                .tag(Tag.STORY_API)
                .summary("카카오 ID로 스토리 목록 조회")
                .description("특정 카카오 ID에 해당하는 스토리 목록을 페이지네이션하여 조회합니다.")
                .pathParameter(
                        parameterWithName("kakaoId").description("가게의 카카오 ID")
                )
                .queryParameter(
                        parameterWithName("size").description("스토리 개수 (기본값: 5) (최소값: 1, 최대값: 50)").optional()
                );

        RestDocsResponse responseDocument = response()
                .responseBodyField(
                        fieldWithPath("stories").type(ARRAY).description("스토리 상세 리스트"),
                        fieldWithPath("stories[].storyId").type(NUMBER).description("스토리 ID"),
                        fieldWithPath("stories[].images").type(ARRAY).description("스토리 이미지 리스트"),
                        fieldWithPath("stories[].images[].imageKey").type(STRING).description("이미지 S3 키"),
                        fieldWithPath("stories[].images[].orderIndex").type(NUMBER).description("이미지 노출 순서"),
                        fieldWithPath("stories[].images[].contentType").type(STRING).description("이미지 MIME 타입"),
                        fieldWithPath("stories[].images[].fileSize").type(NUMBER).description("이미지 파일 크기 (byte)"),
                        fieldWithPath("stories[].images[].url").type(STRING).description("스토리 이미지 CDN URL"),
                        fieldWithPath("stories[].memberId").type(NUMBER).description("회원 ID"),
                        fieldWithPath("stories[].memberNickname").type(STRING).description("회원 닉네임")
                );

        @Test
        void 카카오_ID로_스토리_목록_조회_성공() {
            String kakaoId = "123456";
            int size = 5;
            StoriesDetailResponse response = new StoriesDetailResponse(List.of(
                    new StoryDetailResponse(
                            1L,
                            List.of(new StoryImageResponse("1.png", 0, "image/png", 12345, "https://cdn.test/1.png")),
                            1L,
                            "커찬"
                    ),
                    new StoryDetailResponse(
                            2L,
                            List.of(new StoryImageResponse("2.png", 1, "image/png", 67890, "https://cdn.test/2.png")),
                            2L,
                            "준환"
                    )
            ));
            doReturn(response).when(storyService).getPagedStoryDetails(kakaoId, size);

            var document = document("story/get-stories-by-kakao-id", 200)
                    .request(requestDocument)
                    .response(responseDocument)
                    .build();

            given(document)
                    .queryParam("size", size)
                    .header(HttpHeaders.AUTHORIZATION, accessToken())
                    .when().get("/api/stories/kakao/{kakaoId}", kakaoId)
                    .then().statusCode(200);
        }

        @EnumSource(value = BusinessErrorCode.class, names = {"PRESIGNED_URL_GENERATION_FAILED"})
        @ParameterizedTest
        void 카카오_ID로_스토리_목록_조회_실패(BusinessErrorCode errorCode) {
            String kakaoId = "nonexistent";
            int size = 5;
            doThrow(new BusinessException(errorCode)).when(storyService).getPagedStoryDetails(kakaoId, size);

            var document = document("story/get-stories-by-kakao-id", errorCode)
                    .request(requestDocument)
                    .response(ERROR_RESPONSE)
                    .build();

            given(document)
                    .queryParam("size", size)
                    .when().get("/api/stories/kakao/{kakaoId}", kakaoId)
                    .then().statusCode(errorCode.getStatus().value());
        }
    }
}
