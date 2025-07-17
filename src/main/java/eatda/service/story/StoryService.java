package eatda.service.story;

import eatda.client.map.StoreSearchResult;
import eatda.controller.story.FilteredSearchResult;
import eatda.controller.story.StoryRegisterRequest;
import eatda.domain.member.Member;
import eatda.domain.story.Story;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.repository.member.MemberRepository;
import eatda.repository.story.StoryRepository;
import eatda.service.common.ImageDomain;
import eatda.service.common.ImageService;
import eatda.service.store.StoreService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoreService storeService;
    private final ImageService imageService;
    private final StoryRepository storyRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void registerStory(StoryRegisterRequest request, MultipartFile image, Long memberId) {
        Member member = memberRepository.getById(memberId);
        List<StoreSearchResult> searchResponses = storeService.searchStoreResults(request.query());
        FilteredSearchResult matchedStore = filteredSearchResponse(searchResponses, request.storeKakaoId());
        String imageKey = imageService.upload(image, ImageDomain.STORY);

        Story story = Story.builder()
                .member(member)
                .storeKakaoId(matchedStore.kakaoId())
                .storeName(matchedStore.name())
                .storeAddress(matchedStore.address())
                .storeCategory(matchedStore.category())
                .description(request.description())
                .imageKey(imageKey)
                .build();

        storyRepository.save(story);
    }

    private FilteredSearchResult filteredSearchResponse(List<StoreSearchResult> responses, String storeKakaoId) {
        return responses.stream()
                .filter(store -> store.kakaoId().equals(storeKakaoId))
                .findFirst()
                .map(store -> new FilteredSearchResult(
                        store.kakaoId(),
                        store.name(),
                        store.roadAddress(),
                        store.categoryName()
                ))
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.STORE_NOT_FOUND));
    }
}
