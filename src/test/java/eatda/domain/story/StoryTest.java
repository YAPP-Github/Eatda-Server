package eatda.domain.story;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eatda.domain.ImageKey;
import eatda.domain.member.Member;
import eatda.domain.store.StoreCategory;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

class StoryTest {

    private static final Member MEMBER = Mockito.mock(Member.class);
    private Story.StoryBuilder defaultStoryBuilder;

    @BeforeEach
    void setUpBuilder() {
        this.defaultStoryBuilder = Story.builder()
                .member(MEMBER)
                .storeKakaoId("123")
                .storeName("곱창집")
                .storeRoadAddress("서울시 성동구 왕십리로 1길 12")
                .storeLotNumberAddress("서울시 성동구 성수동1가 685-12")
                .storeCategory(StoreCategory.KOREAN)
                .description("정말 맛있어요")
                .imageKey(new ImageKey("story/image.jpg"));
    }

    @Nested
    class RegisterStory {

        @Test
        void 스토리를_정상적으로_생성한다() {
            Story story = defaultStoryBuilder.build();

            assertThat(story.getStoreName()).isEqualTo("곱창집");
            assertThat(story.getDescription()).isEqualTo("정말 맛있어요");
        }
    }

    @Nested
    class ValidateMember {

        @Test
        void 회원이_null이면_예외가_발생한다() {
            assertThatThrownBy(() -> defaultStoryBuilder
                    .member(null)
                    .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.STORY_MEMBER_REQUIRED.getMessage());
        }
    }

    @Nested
    class ValidateStore {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"\t", "   "})
        void 가게_ID가_비어있으면_예외가_발생한다(String emptyKakaoId) {
            assertThatThrownBy(() -> defaultStoryBuilder
                    .storeKakaoId(emptyKakaoId)
                    .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORE_KAKAO_ID.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"\t", "   "})
        void 가게_이름이_비어있으면_예외가_발생한다(String emptyStoreName) {
            assertThatThrownBy(() -> defaultStoryBuilder
                    .storeName(emptyStoreName)
                    .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORE_NAME.getMessage());
        }

        @Test
        void 도로명_주소가_Null이면_예외가_발생한다() {
            assertThatThrownBy(() -> defaultStoryBuilder
                    .storeRoadAddress(null)
                    .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORE_ADDRESS.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"\t", "   "})
        void 지번_주소가_비어있으면_예외가_발생한다(String emptyLotNumberAddress) {
            assertThatThrownBy(() -> defaultStoryBuilder
                    .storeLotNumberAddress(emptyLotNumberAddress)
                    .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORE_ADDRESS.getMessage());
        }

        @Test
        void 가게_카테고리가_비어있으면_예외가_발생한다() {
            assertThatThrownBy(() -> defaultStoryBuilder
                    .storeCategory(null)
                    .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORE_CATEGORY.getMessage());
        }
    }

    @Nested
    class ValidateStory {

        @Test
        void 설명은_null_이어도_예외가_발생하지_않는다() {
            Story story = defaultStoryBuilder.description(null).build();

            assertThat(story.getDescription()).isNull();
        }

        @ParameterizedTest
        @ValueSource(strings = {"\t", "   ", ""})
        void 설명이_비어있으면_예외가_발생한다(String emptyDescription) {
            assertThatThrownBy(() -> defaultStoryBuilder
                    .description(emptyDescription)
                    .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORY_DESCRIPTION.getMessage());
        }

        @Test
        void 이미지가_비어있으면_예외가_발생한다() {
            assertThatThrownBy(() -> defaultStoryBuilder
                    .imageKey(null)
                    .build()
            ).isInstanceOf(BusinessException.class)
                    .hasMessage(BusinessErrorCode.INVALID_STORY_IMAGE_KEY.getMessage());
        }
    }
}
