package eatda.service.story;

import eatda.controller.story.StoriesDetailResponse;
import eatda.controller.story.StoriesInMemberResponse;
import eatda.controller.story.StoriesResponse;
import eatda.controller.story.StoriesResponse.StoryPreview;
import eatda.controller.story.StoryInMemberResponse;
import eatda.controller.story.StoryRegisterRequest;
import eatda.controller.story.StoryRegisterResponse;
import eatda.controller.story.StoryResponse;
import eatda.domain.ImageKey;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.domain.store.StoreSearchResult;
import eatda.domain.story.Story;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.repository.member.MemberRepository;
import eatda.repository.store.StoreRepository;
import eatda.repository.story.StoryRepository;
import eatda.storage.image.ImageStorage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoryService {

    private static final int PAGE_START_NUMBER = 0;

    private final StoryRepository storyRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final ImageStorage imageStorage;

    @Transactional
    public StoryRegisterResponse registerStory(StoryRegisterRequest request,
                                               StoreSearchResult result,
                                               ImageKey imageKey,
                                               long memberId) {
        Member member = memberRepository.getById(memberId);
        Story story = Story.builder()
                .member(member)
                .storeKakaoId(result.kakaoId())
                .storeName(result.name())
                .storeRoadAddress(result.roadAddress())
                .storeLotNumberAddress(result.lotNumberAddress())
                .storeCategory(result.category())
                .description(request.description())
                .imageKey(imageKey)
                .build();
        storyRepository.save(story);
        return new StoryRegisterResponse(story.getId());
    }

    @Transactional(readOnly = true)
    public StoriesResponse getPagedStoryPreviews(int size) {
        Pageable pageable = PageRequest.of(PAGE_START_NUMBER, size);
        Page<Story> orderByPage = storyRepository.findAllByOrderByCreatedAtDesc(pageable);

        return new StoriesResponse(
                orderByPage.getContent().stream()
                        .map(story -> new StoriesResponse.StoryPreview(
                                story.getId(),
                                imageStorage.getPreSignedUrl(story.getImageKey())
                        ))
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public StoryResponse getStory(long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.STORY_NOT_FOUND));
        Long storeId = storeRepository.findByKakaoId(story.getStoreKakaoId())
                .map(Store::getId)
                .orElse(null);

        return new StoryResponse(
                storeId,
                story.getStoreKakaoId(),
                story.getStoreCategory().getCategoryName(),
                story.getStoreName(),
                story.getAddressDistrict(),
                story.getAddressNeighborhood(),
                story.getDescription(),
                imageStorage.getPreSignedUrl(story.getImageKey()),
                story.getMember().getId(),
                story.getMember().getNickname()
        );
    }

    @Transactional(readOnly = true)
    public StoriesDetailResponse getPagedStoryDetails(String kakaoId, int size) {
        List<Story> stories = storyRepository
                .findAllByStoreKakaoIdOrderByCreatedAtDesc(kakaoId, PageRequest.of(PAGE_START_NUMBER, size))
                .getContent();

        List<StoriesDetailResponse.StoryDetailResponse> responses = stories.stream()
                .map(story -> new StoriesDetailResponse.StoryDetailResponse(
                        story, imageStorage.getPreSignedUrl(story.getImageKey())))
                .toList(); // TODO: N+1 문제 해결
        return new StoriesDetailResponse(responses);
    }

    @Transactional(readOnly = true)
    public StoriesInMemberResponse getStoriesByMemberId(long memberId, int page, int size) {
        List<Story> stories = storyRepository
                .findAllByMemberIdOrderByCreatedAtDesc(memberId, PageRequest.of(page, size))
                .getContent();
        List<StoryInMemberResponse> responses = stories.stream()
                .map(story -> new StoryInMemberResponse(story, imageStorage.getPreSignedUrl(story.getImageKey())))
                .toList();
        return new StoriesInMemberResponse(responses);
    }
}
