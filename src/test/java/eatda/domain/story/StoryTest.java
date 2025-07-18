package eatda.domain.story;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eatda.domain.member.Member;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class StoryTest {

    private static final Member MEMBER = Mockito.mock(Member.class);

    @Nested
    class RegisterStory {

        @Test
        void 스토리를_정상적으로_생성한다() {
            Story story = Story.builder()
                    .member(MEMBER)
                    .storeKakaoId("123")
                    .storeName("곱창집")
                    .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                    .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                    .storeCategory("한식")
                    .description("정말 맛있어요")
                    .imageKey("story/image.jpg")
                    .build();

            assertThat(story.getStoreName()).isEqualTo("곱창집");
            assertThat(story.getDescription()).isEqualTo("정말 맛있어요");
        }
    }

    @Nested
    class ValidateMember {

        @Test
        void 회원이_null이면_예외가_발생한다() {
            assertThatThrownBy(() ->
                    Story.builder()
                            .member(null)
                            .storeKakaoId("123")
                            .storeName("곱창집")
                            .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                            .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                            .storeCategory("한식")
                            .description("정말 맛있어요")
                            .imageKey("story/image.jpg")
                            .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.STORY_MEMBER_REQUIRED.getMessage());
        }
    }

    @Nested
    class ValidateStore {

        @Test
        void 가게_ID가_비어있으면_예외가_발생한다() {
            assertThatThrownBy(() ->
                    Story.builder()
                            .member(MEMBER)
                            .storeKakaoId(" ")
                            .storeName("곱창집")
                            .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                            .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                            .storeCategory("한식")
                            .description("맛있음")
                            .imageKey("story/image.jpg")
                            .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORE_KAKAO_ID.getMessage());
        }

        @Test
        void 가게_이름이_비어있으면_예외가_발생한다() {
            assertThatThrownBy(() ->
                    Story.builder()
                            .member(MEMBER)
                            .storeKakaoId("123")
                            .storeCategory("한식")
                            .storeName(" ")
                            .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                            .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                            .description("맛있음")
                            .imageKey("story/image.jpg")
                            .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORE_NAME.getMessage());
        }

        @Test
        void 도로명_주소가_비어있으면_예외가_발생한다() {
            assertThatThrownBy(() ->
                    Story.builder()
                            .member(MEMBER)
                            .storeKakaoId("123")
                            .storeCategory("한식")
                            .storeName("곱창집")
                            .storeRoadAddress(" ")
                            .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                            .description("맛있음")
                            .imageKey("story/image.jpg")
                            .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORE_ADDRESS.getMessage());
        }

        @Test
        void 지번_주소가_비어있으면_예외가_발생한다() {
            assertThatThrownBy(() ->
                    Story.builder()
                            .member(MEMBER)
                            .storeKakaoId("123")
                            .storeCategory("한식")
                            .storeName("곱창집")
                            .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                            .storeLotNumberAddress(" ")
                            .description("맛있음")
                            .imageKey("story/image.jpg")
                            .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORE_ADDRESS.getMessage());
        }

        @Test
        void 가게_카테고리가_비어있으면_예외가_발생한다() {
            assertThatThrownBy(() ->
                    Story.builder()
                            .member(MEMBER)
                            .storeKakaoId("123")
                            .storeCategory(" ")
                            .storeName("곱창집")
                            .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                            .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                            .description("맛있음")
                            .imageKey("story/image.jpg")
                            .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORE_CATEGORY.getMessage());
        }
    }

    @Nested
    class ValidateStory {

        @Test
        void 설명이_비어있으면_예외가_발생한다() {
            assertThatThrownBy(() ->
                    Story.builder()
                            .member(MEMBER)
                            .storeKakaoId("123")
                            .storeName("곱창집")
                            .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                            .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                            .storeCategory("한식")
                            .description(" ")
                            .imageKey("story/image.jpg")
                            .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORY_DESCRIPTION.getMessage());
        }

        @Test
        void 이미지가_비어있으면_예외가_발생한다() {
            assertThatThrownBy(() ->
                    Story.builder()
                            .member(MEMBER)
                            .storeKakaoId("123")
                            .storeName("곱창집")
                            .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                            .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                            .storeCategory("한식")
                            .description("맛있음")
                            .imageKey(" ")
                            .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORY_IMAGE_KEY.getMessage());
        }
    }
}
