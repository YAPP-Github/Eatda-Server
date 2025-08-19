package eatda.controller.story;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import eatda.controller.BaseControllerTest;
import eatda.domain.member.Member;
import eatda.domain.story.Story;
import eatda.exception.BusinessErrorCode;
import eatda.exception.ErrorResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class StoryControllerTest extends BaseControllerTest {

    @Nested
    class RegisterStory {

        @Test
        void 스토리를_등록할_수_있다() {
            StoryRegisterRequest request = new StoryRegisterRequest("농민백암순대", "123", "여기 진짜 맛있어요!", new ArrayList<>());

            StoryRegisterResponse response = given()
                    .contentType("multipart/form-data")
                    .header("Authorization", accessToken())
                    .contentType("application/json")
                    .body(request)
                    .when()
                    .post("/api/stories")
                    .then()
                    .statusCode(201)
                    .extract().as(StoryRegisterResponse.class);

            assertThat(response.storyId()).isNotZero();
        }
    }

    @Nested
    class GetStories {

        @Test
        void 스토리_목록을_조회할_수_있다() {
            Member member = memberGenerator.generateRegisteredMember("test", "test@kakao.com", "812", "01081231234");
            LocalDateTime createdAt = LocalDateTime.of(2025, 8, 5, 12, 0, 0);
            Story story1 = storyGenerator.generate(member, "123456", "진또곱창집", createdAt);
            Story story2 = storyGenerator.generate(member, "654321", "또진곱창집", createdAt.plusHours(1));

            StoriesResponse response = given()
                    .queryParam("size", 5)
                    .when()
                    .get("/api/stories")
                    .then().statusCode(200)
                    .extract().as(StoriesResponse.class);

            assertAll(
                    () -> assertThat(response.stories()).hasSize(2),
                    () -> assertThat(response.stories().get(0).storyId()).isEqualTo(story2.getId()),
                    () -> assertThat(response.stories().get(1).storyId()).isEqualTo(story1.getId())
            );
        }
    }

    @Nested
    class GetStory {

        @Test
        void 해당_스토리를_상세_조회할_수_있다() {
            Member member = memberGenerator.generateRegisteredMember("test", "test@kakao.com", "812", "01081231234");
            Story story = storyGenerator.generate(member, "123456", "진또곱창집", "서울시 성동구 성수동 123-45", "곱창은 여기");

            StoryResponse response = given()
                    .pathParam("storyId", story.getId())
                    .when()
                    .get("/api/stories/{storyId}")
                    .then()
                    .statusCode(200)
                    .extract().as(StoryResponse.class);

            assertAll(
                    () -> assertThat(response.storeId()).isNull(),
                    () -> assertThat(response.storeKakaoId()).isEqualTo("123456"),
                    () -> assertThat(response.storeName()).isEqualTo("진또곱창집"),
                    () -> assertThat(response.storeDistrict()).isEqualTo("성동구"),
                    () -> assertThat(response.storeNeighborhood()).isEqualTo("성수동"),
                    () -> assertThat(response.description()).isEqualTo("곱창은 여기"),
                    () -> assertThat(response.memberId()).isEqualTo(member.getId()),
                    () -> assertThat(response.memberNickname()).isEqualTo("test")
            );
        }

        @Test
        void 존재하지_않는_스토리를_조회하면_404_응답한다() {
            long nonexistentId = 999L;

            ErrorResponse response = given().pathParam("storyId", nonexistentId)
                    .when().get("/api/stories/{storyId}")
                    .then()
                    .statusCode(404)
                    .extract().as(ErrorResponse.class);

            assertAll(
                    () -> assertThat(response.errorCode()).isEqualTo(BusinessErrorCode.STORY_NOT_FOUND.getCode()),
                    () -> assertThat(response.message()).isEqualTo(BusinessErrorCode.STORY_NOT_FOUND.getMessage())
            );
        }
    }

    @Nested
    class GetStoriesByMemberId {

        @Test
        void 회원의_스토리_목록을_조회할_수_있다() {
            Member member = memberGenerator.generateRegisteredMember("nickname", "abc@kakao.com", "123", "01012345679");
            LocalDateTime startAt = LocalDateTime.of(2025, 8, 5, 12, 0, 0);
            Story story1 = storyGenerator.generate(member, "123456", "진또곱창집", startAt);
            Story story2 = storyGenerator.generate(member, "654321", "또진곱창집", startAt.plusHours(1));
            int page = 0;
            int size = 5;

            StoriesInMemberResponse response = given()
                    .header(HttpHeaders.AUTHORIZATION, accessToken(member))
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .when()
                    .get("/api/stories/member")
                    .then().statusCode(200)
                    .extract().as(StoriesInMemberResponse.class);

            assertAll(
                    () -> assertThat(response.stories()).hasSize(2),
                    () -> assertThat(response.stories().get(0).id()).isEqualTo(story2.getId()),
                    () -> assertThat(response.stories().get(1).id()).isEqualTo(story1.getId())
            );
        }

        @Nested
        class GetStoriesByKakaoId {

            @Test
            void 카카오ID로_스토리_목록을_조회할_수_있다() {
                String kakaoId = "123456";
                LocalDateTime startAt = LocalDateTime.of(2025, 8, 5, 12, 0, 0);
                Member member = memberGenerator.generateRegisteredMember("test", "test@kakao.com", "812",
                        "01081231234");
                Story story1 = storyGenerator.generate(member, kakaoId, "진또곱창집", startAt);
                Story story2 = storyGenerator.generate(member, kakaoId, "진또곱창집", startAt.plusHours(1));

                StoriesDetailResponse response = given()
                        .pathParam("kakaoId", kakaoId)
                        .queryParam("size", 5)
                        .when()
                        .get("/api/stories/kakao/{kakaoId}")
                        .then().statusCode(200)
                        .extract().as(StoriesDetailResponse.class);

                assertAll(
                        () -> assertThat(response.stories()).hasSize(2),
                        () -> assertThat(response.stories().get(0).storyId()).isEqualTo(story2.getId()),
                        () -> assertThat(response.stories().get(0).memberId()).isEqualTo(member.getId()),
                        () -> assertThat(response.stories().get(0).memberNickname()).isEqualTo(member.getNickname()),
                        () -> assertThat(response.stories().get(1).storyId()).isEqualTo(story1.getId()),
                        () -> assertThat(response.stories().get(1).memberId()).isEqualTo(member.getId()),
                        () -> assertThat(response.stories().get(1).memberNickname()).isEqualTo(member.getNickname())
                );
            }
        }
    }
}
