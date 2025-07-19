package eatda.service.story;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eatda.client.map.StoreSearchResult;
import eatda.controller.story.StoryRegisterRequest;
import eatda.domain.member.Member;
import eatda.domain.story.Story;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.repository.image.ImageDomain;
import eatda.service.BaseServiceTest;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class StoryServiceTest extends BaseServiceTest {

    @Autowired
    private StoryService storyService;

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
            when(imageRepository.upload(image, ImageDomain.STORY)).thenReturn("image-key");

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
    class GetPagedStoryPreviews extends BaseServiceTest {

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

            when(imageRepository.getPresignedUrl("image-key-1")).thenReturn("https://s3.com/story1.jpg");
            when(imageRepository.getPresignedUrl("image-key-2")).thenReturn("https://s3.com/story2.jpg");

            var response = storyService.getPagedStoryPreviews();

            assertThat(response.stories()).hasSize(2);
            assertThat(response.stories())
                    .extracting("imageUrl")
                    .containsExactly(
                            "https://s3.com/story2.jpg",
                            "https://s3.com/story1.jpg"
                    );
        }
    }
}
