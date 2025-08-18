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
import eatda.domain.store.District;
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

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoryImageRepository storyImageRepository;

    @MockBean
    private FileClient fileClient;

    @Nested
    class RegisterStory {

        private Member member;
        private StoreSearchResult storeSearchResult;

        @BeforeEach
        void setUp() {
            member = memberGenerator.generate("12345");
            storeSearchResult = new StoreSearchResult(
                    "123", StoreCategory.KOREAN, "02-755-5232", "곱창", "http://place.map.kakao.com/123",
                    "서울시 강남구 역삼동 123-45",
                    "서울시 강남구 사사로 3길 12-24",
                    District.GANGNAM, 37.5665, 126.9780);
        }

        @Test
        @Transactional
        void 스토리_등록에_성공한다() {
            StoryRegisterRequest request =
                    new StoryRegisterRequest("곱창", "123", "미쳤다 여기", List.of());

            var response = storyService.registerStory(
                    request, storeSearchResult, ImageDomain.STORY, member.getId());

            Story savedStory = storyRepository.findById(response.storyId()).orElseThrow();
            assertAll(
                    () -> assertThat(savedStory.getMember().getId()).isEqualTo(member.getId()),
                    () -> assertThat(savedStory.getStoreKakaoId()).isEqualTo("123"),
                    () -> assertThat(savedStory.getStoreName()).isEqualTo("곱창"),
                    () -> assertThat(savedStory.getStoreRoadAddress()).isEqualTo("서울시 강남구 사사로 3길 12-24"),
                    () -> assertThat(savedStory.getStoreLotNumberAddress()).isEqualTo("서울시 강남구 역삼동 123-45"),
                    () -> assertThat(savedStory.getStoreCategory()).isEqualTo(StoreCategory.KOREAN),
                    () -> assertThat(savedStory.getDescription()).isEqualTo("미쳤다 여기"),
                    () -> assertThat(savedStory.getImages()).isEmpty()
            );
        }

        @Test
        @Transactional
        void 스토리_등록_시_이미지도_함께_저장된다() {
            UploadedImageDetail image2 =
                    new UploadedImageDetail("temp-key-2", 2L, "image/jpeg", 2000L);
            UploadedImageDetail image1 =
                    new UploadedImageDetail("temp-key-1", 1L, "image/jpeg", 1000L);
            StoryRegisterRequest request =
                    new StoryRegisterRequest("곱창", "123", "미쳤다 여기", List.of(image2, image1));

            List<String> permanentKeys = List.of("permanent/path/1", "permanent/path/2");
            given(fileClient.moveTempFilesToPermanent(any(String.class), anyLong(), anyList()))
                    .willReturn(permanentKeys);

            var response = storyService.registerStory(
                    request, storeSearchResult, ImageDomain.STORY, member.getId());

            Story savedStory = storyRepository.findById(response.storyId()).orElseThrow();

            assertAll(
                    () -> assertThat(savedStory.getImages()).hasSize(2),
                    () -> assertThat(savedStory.getImages()).extracting(img -> img.getOrderIndex())
                            .containsExactly(1L, 2L),
                    () -> assertThat(savedStory.getImages()).extracting(img -> img.getImageKey())
                            .containsExactlyElementsOf(permanentKeys)
            );
        }
    }

    @Nested
    class GetPagedStoryPreviews {

        @Test
        void 스토리_목록을_조회할_수_있다() {
            Member member = memberGenerator.generate("12345");
            Story story1 = storyGenerator.generate(member, "1", "곱창집");
            storyImageGenerator.generate(story1, "key1-2", 2L, "image/png", 200L);
            storyImageGenerator.generate(story1, "key1-1", 1L, "image/png", 100L);

            Story story2 = storyGenerator.generate(member, "2", "순대국밥집");

            var response = storyService.getPagedStoryPreviews(5);

            assertThat(response.stories()).hasSize(2)
                    .extracting(StoriesResponse.StoryPreview::storyId)
                    .containsExactly(story2.getId(), story1.getId());

            StoriesResponse.StoryPreview storyPreview1 = response.stories().stream()
                    .filter(p -> p.storyId() == story1.getId())
                    .findFirst().orElseThrow();

            assertThat(storyPreview1.images()).hasSize(2)
                    .isSortedAccordingTo(Comparator.comparingLong(StoryImageResponse::orderIndex));
            assertThat(storyPreview1.images().get(0).orderIndex()).isEqualTo(1L);
        }
    }

    @Nested
    class GetStory {

        private Member member;
        private Story story;

        @BeforeEach
        void setUp() {
            member = memberGenerator.generate("99999");
            story = storyGenerator.generate(member, "123456", "진또곱창집");
        }

        @Test
        void 스토리_상세_정보를_조회할_때_스토어ID가_없으면_NULL로_반환된다() {
            StoryResponse response = storyService.getStory(story.getId());

            assertAll(
                    () -> assertThat(response.storeId()).isNull(),
                    () -> assertThat(response.storeKakaoId()).isEqualTo("123456")
            );
        }

        @Test
        void 스토리_상세_정보를_조회할_때_스토어ID가_있으면_해당_값을_반환한다() {
            Store store = storeRepository.save(
                    storeGenerator.generate("123456", "진또곱창집"));

            StoryResponse response = storyService.getStory(story.getId());

            assertAll(
                    () -> assertThat(response.storeId()).isEqualTo(store.getId()),
                    () -> assertThat(response.storeKakaoId()).isEqualTo("123456")
            );
        }

        @Test
        void 존재하지_않는_스토리ID로_조회하면_예외가_발생한다() {
            assertThatThrownBy(() -> storyService.getStory(999999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(BusinessErrorCode.STORY_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class GetPagedStoryDetails {

        @Test
        void 카카오ID로_스토리_목록을_조회할_수_있다() {
            Member member = memberGenerator.generate("99999");
            String kakaoId = "123456";

            Story story1 = storyGenerator.generate(member, kakaoId, "진또곱창집");
            storyImageGenerator.generate(story1, "key1-2", 2L, "image/jpeg", 200L);
            storyImageGenerator.generate(story1, "key1-1", 1L, "image/jpeg", 100L);

            Story story2 = storyGenerator.generate(member, kakaoId, "진또곱창집");
            storyGenerator.generate(member, "other-id", "다른집");

            var response = storyService.getPagedStoryDetails(kakaoId, 5);

            assertThat(response.stories()).hasSize(2)
                    .extracting(StoriesDetailResponse.StoryDetailResponse::storyId)
                    .containsExactly(story2.getId(), story1.getId());

            StoriesDetailResponse.StoryDetailResponse detailResponse1 = response.stories().stream()
                    .filter(d -> d.storyId() == story1.getId())
                    .findFirst().orElseThrow();

            assertThat(detailResponse1.images()).hasSize(2)
                    .isSortedAccordingTo(Comparator.comparingLong(StoryImageResponse::orderIndex));
            assertThat(detailResponse1.images().get(0).orderIndex()).isEqualTo(1L);

            StoriesDetailResponse.StoryDetailResponse detailResponse2 = response.stories().stream()
                    .filter(d -> d.storyId() == story2.getId())
                    .findFirst().orElseThrow();
            assertThat(detailResponse2.images()).isEmpty();
        }
    }

    @Nested
    class GetPagedStoryByMemberId {

        @Test
        void 회원_ID로_스토리_목록을_조회할_수_있다() {
            Member member = memberGenerator.generate("12345");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 23, 10, 0);
            Story story1 = storyGenerator.generate(member, "123456", "곱창집", startAt);
            Story story2 = storyGenerator.generate(member, "123457", "순대국밥집", startAt.plusDays(1));

            var response = storyService.getPagedStoryByMemberId(member.getId(), 0, 5);

            assertAll(
                    () -> assertThat(response.stories()).hasSize(2),
                    () -> assertThat(response.stories().get(0).id()).isEqualTo(story2.getId()),
                    () -> assertThat(response.stories().get(0).storeName()).isEqualTo(story2.getStoreName()),
                    () -> assertThat(response.stories().get(1).id()).isEqualTo(story1.getId()),
                    () -> assertThat(response.stories().get(1).storeName()).isEqualTo(story1.getStoreName())
            );
        }

        @Test
        void 회원_ID로_스토리_목록을_페이지네이션할_수_있다() {
            Member member = memberGenerator.generate("12345");
            LocalDateTime startAt = LocalDateTime.of(2025, 7, 23, 10, 0);
            Story story1 = storyGenerator.generate(member, "123456", "곱창집", startAt);
            Story story2 = storyGenerator.generate(member, "123457", "순대국밥집", startAt.plusDays(1));
            Story story3 = storyGenerator.generate(member, "123458", "김밥집", startAt.plusDays(2));

            var response = storyService.getPagedStoryByMemberId(member.getId(), 1, 2);

            assertAll(
                    () -> assertThat(response.stories()).hasSize(1),
                    () -> assertThat(response.stories().get(0).id()).isEqualTo(story1.getId()),
                    () -> assertThat(response.stories().get(0).storeName()).isEqualTo(story1.getStoreName())
            );
        }

        @Test
        void 회원_ID로_스토리_목록을_조회할_때_존재하지_않는_ID를_요청하면_빈_목록을_반환한다() {
            long nonExistentMemberId = 999L;

            var response = storyService.getPagedStoryByMemberId(nonExistentMemberId, 0, 5);

            assertThat(response.stories()).isEmpty();
        }
    }
}
