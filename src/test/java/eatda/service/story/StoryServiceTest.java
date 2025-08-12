package eatda.service.story;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import eatda.controller.story.StoriesDetailResponse;
import eatda.controller.story.StoriesResponse.StoryPreview;
import eatda.controller.story.StoryRegisterRequest;
import eatda.controller.story.StoryRegisterResponse;
import eatda.controller.story.StoryResponse;
import eatda.domain.ImageKey;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.domain.store.StoreSearchResult;
import eatda.domain.story.Story;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.service.BaseServiceTest;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StoryServiceTest extends BaseServiceTest {

    @Autowired
    private StoryService storyService;

    @Test
    void 존재하지_않는_카카오ID로_조회하면_빈_목록을_반환한다() {
        String nonExistentKakaoId = "non-existent";

        var response = storyService.getPagedStoryDetails(nonExistentKakaoId, 5);

        assertThat(response.stories()).isEmpty();
    }

    @Nested
    class RegisterStory {

        @Test
        void 스토리_등록에_성공한다() {
            Member member = memberGenerator.generate("12345");
            StoryRegisterRequest request = new StoryRegisterRequest("곱창", "123", "미쳤다 여기");
            StoreSearchResult result = new StoreSearchResult(
                    "123", StoreCategory.KOREAN, "02-755-5232", "곱창", "http://place.map.kakao.com/123",
                    "서울시 강남구 사사로 3길 12-24", "서울시 강남구 역삼동 123-45", 37.5665, 126.9780);
            ImageKey imageKey = new ImageKey("image-key");

            StoryRegisterResponse response = storyService.registerStory(request, result, imageKey, member.getId());

            Story savedStory = storyRepository.findById(response.storyId()).orElseThrow();
            assertAll(
                    () -> assertThat(savedStory.getMember().getId()).isEqualTo(member.getId()),
                    () -> assertThat(savedStory.getStoreKakaoId()).isEqualTo("123"),
                    () -> assertThat(savedStory.getStoreName()).isEqualTo("곱창"),
                    () -> assertThat(savedStory.getStoreRoadAddress()).isEqualTo("서울시 강남구 역삼동 123-45"),
                    () -> assertThat(savedStory.getStoreLotNumberAddress()).isEqualTo("서울시 강남구 사사로 3길 12-24"),
                    () -> assertThat(savedStory.getStoreCategory()).isEqualTo(StoreCategory.KOREAN),
                    () -> assertThat(savedStory.getDescription()).isEqualTo("미쳤다 여기"),
                    () -> assertThat(savedStory.getImageKey()).isEqualTo(imageKey)
            );
        }
    }

    @Nested
    class GetPagedStoryPreviews {

        @Test
        void 스토리_목록을_조회할_수_있다() {
            Member member = memberGenerator.generate("12345");
            Story story1 = Story.builder()
                    .member(member)
                    .storeKakaoId("1")
                    .storeName("곱창집")
                    .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                    .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                    .storeCategory(StoreCategory.KOREAN)
                    .description("미쳤다 진짜")
                    .imageKey(new ImageKey("image-key-1"))
                    .build();
            Story story2 = Story.builder()
                    .member(member)
                    .storeKakaoId("2")
                    .storeName("순대국밥집")
                    .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                    .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                    .storeCategory(StoreCategory.KOREAN)
                    .description("뜨끈한 국밥 최고")
                    .imageKey(new ImageKey("image-key-2"))
                    .build();
            storyRepository.saveAll(List.of(story1, story2));

            var response = storyService.getPagedStoryPreviews(5);

            assertThat(response.stories())
                    .hasSize(2)
                    .extracting(StoryPreview::storyId)
                    .containsExactlyInAnyOrder(story2.getId(), story1.getId());
        }
    }

    @Nested
    class GetStory {

        @Test
        void 스토리_상세_정보를_조회할_때_스토어ID가_없으면_NULL로_반환된다() {
            Member member = memberGenerator.generate("99999");

            Story story = Story.builder()
                    .member(member)
                    .storeKakaoId("123456")
                    .storeName("진또곱창집")
                    .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                    .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                    .storeCategory(StoreCategory.KOREAN)
                    .description("곱창은 여기")
                    .imageKey(new ImageKey("story-image-key"))
                    .build();
            storyRepository.save(story);

            when(externalImageStorage.getPreSignedUrl(new ImageKey("story-image-key")))
                    .thenReturn("https://s3.bucket.com/story/dummy/1.jpg");

            StoryResponse response = storyService.getStory(story.getId());

            assertAll(
                    () -> assertThat(response.storeId()).isNull(),
                    () -> assertThat(response.storeKakaoId()).isEqualTo("123456"),
                    () -> assertThat(response.category()).isEqualTo("한식"),
                    () -> assertThat(response.storeName()).isEqualTo("진또곱창집"),
                    () -> assertThat(response.storeDistrict()).isEqualTo("성동구"),
                    () -> assertThat(response.storeNeighborhood()).isEqualTo("성수동1가"),
                    () -> assertThat(response.description()).isEqualTo("곱창은 여기"),
                    () -> assertThat(response.imageUrl()).isEqualTo("https://s3.bucket.com/story/dummy/1.jpg"),
                    () -> assertThat(response.memberId()).isEqualTo(member.getId()),
                    () -> assertThat(response.memberNickname()).isEqualTo(member.getNickname())
            );
        }

        @Test
        void 스토리_상세_정보를_조회할_때_스토어ID가_있으면_해당_ID값을_반환한다() {
            Member member = memberGenerator.generate("99999");
            Store store = storeGenerator.generate("123456", "서울시 성북구 장위동 123-45");
            Story story = Story.builder()
                    .member(member)
                    .storeKakaoId("123456")
                    .storeName("진또곱창집")
                    .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                    .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                    .storeCategory(StoreCategory.KOREAN)
                    .description("곱창은 여기")
                    .imageKey(new ImageKey("story-image-key"))
                    .build();
            storyRepository.save(story);

            when(externalImageStorage.getPreSignedUrl(new ImageKey("story-image-key")))
                    .thenReturn("https://s3.bucket.com/story/dummy/1.jpg");

            StoryResponse response = storyService.getStory(story.getId());

            assertAll(
                    () -> assertThat(response.storeId()).isEqualTo(store.getId()),
                    () -> assertThat(response.storeKakaoId()).isEqualTo("123456"),
                    () -> assertThat(response.category()).isEqualTo("한식"),
                    () -> assertThat(response.storeName()).isEqualTo("진또곱창집"),
                    () -> assertThat(response.storeDistrict()).isEqualTo("성동구"),
                    () -> assertThat(response.storeNeighborhood()).isEqualTo("성수동1가"),
                    () -> assertThat(response.description()).isEqualTo("곱창은 여기"),
                    () -> assertThat(response.imageUrl()).isEqualTo("https://s3.bucket.com/story/dummy/1.jpg"),
                    () -> assertThat(response.memberId()).isEqualTo(member.getId()),
                    () -> assertThat(response.memberNickname()).isEqualTo(member.getNickname())
            );
        }

        @Test
        void 존재하지_않는_스토리ID를_조회하면_예외가_발생한다() {
            long invalidId = 999L;

            assertThatThrownBy(() -> storyService.getStory(invalidId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(BusinessErrorCode.STORY_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class GetPagedStoryDetails {

        @Test
        void 카카오ID로_스토리_목록을_조회할_수_있다() {
            String kakaoId = "123456";
            Member member = memberGenerator.generate("12345");
            Story story1 = Story.builder()
                    .member(member)
                    .storeKakaoId(kakaoId)
                    .storeName("곱창집")
                    .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                    .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                    .storeCategory(StoreCategory.KOREAN)
                    .description("미쳤다 진짜")
                    .imageKey(new ImageKey("image-key-1"))
                    .build();
            Story story2 = Story.builder()
                    .member(member)
                    .storeKakaoId(kakaoId)
                    .storeName("순대국밥집")
                    .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                    .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                    .storeCategory(StoreCategory.KOREAN)
                    .description("뜨끈한 국밥 최고")
                    .imageKey(new ImageKey("image-key-2"))
                    .build();
            storyRepository.save(story1);
            storyRepository.save(story2);
            when(externalImageStorage.getPreSignedUrl(new ImageKey("image-key-1")))
                    .thenReturn("https://s3.bucket.com/story/dummy/1.jpg");
            when(externalImageStorage.getPreSignedUrl(new ImageKey("image-key-2")))
                    .thenReturn("https://s3.bucket.com/story/dummy/2.jpg");

            var response = storyService.getPagedStoryDetails(kakaoId, 5);

            assertThat(response.stories())
                    .hasSize(2)
                    .extracting(StoriesDetailResponse.StoryDetailResponse::storyId)
                    .containsExactlyInAnyOrder(story2.getId(), story1.getId());
        }

        @Test
        void 카카오ID로_스토리_목록을_조회할_때_특정_스토리만_반환한다() {
            String kakaoId = "123456";
            Member member = memberGenerator.generate("12345");
            Story story1 = Story.builder()
                    .member(member)
                    .storeKakaoId(kakaoId)
                    .storeName("곱창집")
                    .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                    .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                    .storeCategory(StoreCategory.KOREAN)
                    .description("미쳤다 진짜")
                    .imageKey(new ImageKey("image-key-1"))
                    .build();
            Story story2 = Story.builder()
                    .member(member)
                    .storeKakaoId("different-kakao-id")
                    .storeName("순대국밥집")
                    .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                    .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                    .storeCategory(StoreCategory.KOREAN)
                    .description("뜨끈한 국밥 최고")
                    .imageKey(new ImageKey("image-key-2"))
                    .build();
            storyRepository.save(story1);
            storyRepository.save(story2);
            when(externalImageStorage.getPreSignedUrl(new ImageKey("image-key-1")))
                    .thenReturn("https://s3.bucket.com/story/dummy/1.jpg");
            when(externalImageStorage.getPreSignedUrl(new ImageKey("image-key-2")))
                    .thenReturn("https://s3.bucket.com/story/dummy/2.jpg");

            var response = storyService.getPagedStoryDetails(kakaoId, 5);

            assertThat(response.stories())
                    .hasSize(1)
                    .extracting(StoriesDetailResponse.StoryDetailResponse::storyId)
                    .containsExactlyInAnyOrder(story1.getId());
        }
    }
}
