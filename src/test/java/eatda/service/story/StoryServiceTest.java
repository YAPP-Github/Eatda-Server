package eatda.service.story;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eatda.client.map.StoreSearchResult;
import eatda.controller.story.StoryRegisterRequest;
import eatda.domain.member.Member;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.service.BaseServiceTest;
import eatda.service.common.ImageDomain;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

public class StoryServiceTest {

    @Nested
    class RegisterStory extends BaseServiceTest {

        private StoryService storyService;

        @BeforeEach
        void setUp() {
            storyService = new StoryService(storeService, imageService, storyRepository, memberRepository);
        }

        @Test
        void 스토리_등록에_성공한다() {
            Member member = memberGenerator.generate("12345");
            StoryRegisterRequest request = new StoryRegisterRequest("곱창", "123", "미쳤다 여기");

            MultipartFile image = mock(MultipartFile.class);
            when(imageService.upload(image, ImageDomain.STORY)).thenReturn("image-key");

            StoreSearchResult store = new StoreSearchResult(
                    "123", "FD6", "음식점 > 한식", "010-1234-5678",
                    "곱창집", "http://example.com",
                    "서울 강남구", "서울 강남구", 37.0, 127.0
            );

            when(storeService.searchStoreResults(request.query())).thenReturn(List.of(store));

            assertDoesNotThrow(() ->
                    storyService.registerStory(request, image, member.getId())
            );
        }

        @Test
        void 클라이언트_요청과_일치하는_가게가_없으면_실패한다() {
            Member member = memberGenerator.generate("12345");
            StoryRegisterRequest request = new StoryRegisterRequest("곱창", "999", "미쳤다 여기");

            MultipartFile image = mock(MultipartFile.class);
            when(storeService.searchStoreResults(request.query())).thenReturn(List.of());

            assertThatThrownBy(() ->
                    storyService.registerStory(request, image, member.getId())
            ).isInstanceOf(BusinessException.class)
                    .hasMessageContaining(BusinessErrorCode.STORE_NOT_FOUND.getMessage());
        }
    }
}
