package eatda.service.story;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eatda.client.map.StoreSearchResult;
import eatda.controller.story.StoryRegisterRequest;
import eatda.controller.story.StoryResponse;
import eatda.domain.member.Member;
import eatda.domain.story.Story;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.repository.story.StoryRepository;
import eatda.service.BaseServiceTest;
import eatda.service.common.ImageDomain;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class StoryServiceTest extends BaseServiceTest {

    @Autowired
    private StoryService storyService;
    @Autowired
    private StoryRepository storyRepository;

    @Nested
    class RegisterStory {

        @Test
        void 스토리_등록에_성공한다() {
            Member member = memberGenerator.generate("12345");
            StoryRegisterRequest request = new StoryRegisterRequest("곱창", "123", "미쳤다 여기");
            MultipartFile image = mock(MultipartFile.class);

            StoreSearchResult store = new StoreSearchResult(
                    "123", "FD6", "음식점 > 한식", "010-1234-5678",
                    "곱창집", "http://example.com",
                    "서울 강남구", "서울 강남구", 37.0, 127.0
            );
            doReturn(List.of(store)).when(mapClient).searchShops(request.query());
            when(imageService.upload(image, ImageDomain.STORY)).thenReturn("image-key");

            assertDoesNotThrow(() -> storyService.registerStory(request, image, member.getId()));
        }

        @Test
        void 클라이언트_요청과_일치하는_가게가_없으면_실패한다() {
            Member member = memberGenerator.generate("12345");
            StoryRegisterRequest request = new StoryRegisterRequest("곱창", "999", "미쳤다 여기");

            MultipartFile image = mock(MultipartFile.class);
            doReturn(Collections.emptyList()).when(mapClient).searchShops(request.query());

            assertThatThrownBy(() -> storyService.registerStory(request, image, member.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(BusinessErrorCode.STORE_NOT_FOUND.getMessage());
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
                    .storeAddress("서울시")
                    .storeCategory("한식")
                    .description("미쳤다 진짜")
                    .imageKey("image-key-1")
                    .build();

            Story story2 = Story.builder()
                    .member(member)
                    .storeKakaoId("2")
                    .storeName("순대국밥집")
                    .storeAddress("부산시")
                    .storeCategory("한식")
                    .description("뜨끈한 국밥 최고")
                    .imageKey("image-key-2")
                    .build();

            storyRepository.saveAll(List.of(story1, story2));

            when(imageService.getPresignedUrl("image-key-1")).thenReturn("https://s3.bucket.com/story/dummy/1.jpg");
            when(imageService.getPresignedUrl("image-key-2")).thenReturn("https://s3.bucket.com/story/dummy/2.jpg");

            var response = storyService.getPagedStoryPreviews(5);

            assertThat(response.stories()).hasSize(2);
            assertThat(response.stories())
                    .extracting("imageUrl")
                    .containsExactlyInAnyOrder(
                            "https://s3.bucket.com/story/dummy/2.jpg",
                            "https://s3.bucket.com/story/dummy/1.jpg"
                    );
        }
    }

    @Nested
    class GetStory {

        @Test
        void 스토리_상세_정보를_조회할_수_있다() {
            Member member = memberGenerator.generate("99999");

            Story story = Story.builder()
                    .member(member)
                    .storeKakaoId("123456")
                    .storeName("진또곱창집")
                    .storeAddress("서울특별시 성동구 성수동1가")
                    .storeCategory("한식")
                    .description("곱창은 여기")
                    .imageKey("story-image-key")
                    .build();

            storyRepository.save(story);

            when(imageService.getPresignedUrl("story-image-key"))
                    .thenReturn("https://s3.bucket.com/story/dummy/1.jpg");

            StoryResponse response = storyService.getStory(story.getId());

            assertThat(response.storeKakaoId()).isEqualTo("123456");
            assertThat(response.category()).isEqualTo("한식");
            assertThat(response.storeName()).isEqualTo("진또곱창집");
            assertThat(response.storeAddress()).isEqualTo("서울특별시 성동구 성수동1가");
            assertThat(response.description()).isEqualTo("곱창은 여기");
            assertThat(response.imageUrl()).isEqualTo("https://s3.bucket.com/story/dummy/1.jpg");
        }

        @Test
        void 존재하지_않는_스토리ID를_조회하면_예외가_발생한다() {
            long invalidId = 999L;

            assertThatThrownBy(() -> storyService.getStory(invalidId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(BusinessErrorCode.STORY_NOT_FOUND.getMessage());
        }
    }
}
