package eatda.fixture;

import eatda.domain.ImageKey;
import eatda.domain.member.Member;
import eatda.domain.store.StoreCategory;
import eatda.domain.story.Story;
import eatda.repository.story.StoryRepository;
import eatda.util.DomainUtils;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class StoryGenerator {

    private static final StoreCategory DEFAULT_CATEGORY = StoreCategory.OTHER;
    private static final String DEFAULT_DESCRIPTION = "이곳은 정말 맛있어요!";
    private static final String DEFAULT_ROAD_ADDRESS = "서울시 강남구 준비로 11길 123";
    private static final String DEFAULT_LOT_NUMBER_ADDRESS = "서울시 강남구 역삼동 123-45";
    private static final ImageKey DEFAULT_IMAGE_KEY = new ImageKey("default-story-image.jpg");

    private final StoryRepository storyRepository;

    public StoryGenerator(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public Story generate(Member member, String kakaoId, String storeName) {
        Story story = create(member, kakaoId, storeName, DEFAULT_LOT_NUMBER_ADDRESS, DEFAULT_DESCRIPTION);
        return storyRepository.save(story);
    }

    public Story generate(Member member, String kakaoId, String storeName, LocalDateTime createdAt) {
        Story story = create(member, kakaoId, storeName, DEFAULT_LOT_NUMBER_ADDRESS, DEFAULT_DESCRIPTION);
        DomainUtils.setCreatedAt(story, createdAt);
        return storyRepository.save(story);
    }

    public Story generate(Member member, String kakaoId, String storeName, String lotNumberAddress,
                          String description) {
        Story story = create(member, kakaoId, storeName, lotNumberAddress, description);
        return storyRepository.save(story);
    }

    private Story create(Member member, String kakaoId, String storeName, String lotNumberAddress, String description) {
        return Story.builder()
                .member(member)
                .storeKakaoId(kakaoId)
                .storeCategory(DEFAULT_CATEGORY)
                .storeName(storeName)
                .storeRoadAddress(DEFAULT_ROAD_ADDRESS)
                .storeLotNumberAddress(lotNumberAddress)
                .description(description)
                .imageKey(DEFAULT_IMAGE_KEY)
                .build();
    }
}
