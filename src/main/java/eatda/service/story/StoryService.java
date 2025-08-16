package eatda.service.story;

import eatda.client.file.FileClient;
import eatda.controller.story.StoriesDetailResponse;
import eatda.controller.story.StoriesResponse;
import eatda.controller.story.StoryImageResponse;
import eatda.controller.story.StoryRegisterRequest;
import eatda.controller.story.StoryRegisterResponse;
import eatda.controller.story.StoryResponse;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.domain.store.StoreSearchResult;
import eatda.domain.story.Story;
import eatda.domain.story.StoryImage;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.repository.member.MemberRepository;
import eatda.repository.store.StoreRepository;
import eatda.repository.story.StoryImageRepository;
import eatda.repository.story.StoryRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final StoryImageRepository storyImageRepository;
    private final FileClient fileClient;

    @Transactional
    public StoryRegisterResponse registerStory(StoryRegisterRequest request,
                                               StoreSearchResult result,
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
                .build();
        storyRepository.save(story);

        List<StoryRegisterRequest.UploadedImageDetail> sortedImages = request.images().stream()
                .sorted(Comparator.comparingLong(StoryRegisterRequest.UploadedImageDetail::orderIndex))
                .toList();

        List<String> tempKeys = sortedImages.stream()
                .map(StoryRegisterRequest.UploadedImageDetail::imageKey)
                .toList();

        List<String> permanentKeys = fileClient.moveTempFilesToPermanent("story", story.getId(), tempKeys);

        List<StoryImage> storyImages = IntStream.range(0, sortedImages.size())
                .mapToObj(i -> {
                    var detail = sortedImages.get(i);
                    return new StoryImage(
                            story,
                            permanentKeys.get(i),
                            detail.orderIndex(),
                            detail.contentType(),
                            detail.fileSize());
                })
                .toList();

        storyImageRepository.saveAll(storyImages);

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
                                story.getImages().stream()
                                        .map(StoryImageResponse::new)
                                        .sorted(Comparator.comparingLong(StoryImageResponse::orderIndex))
                                        .toList()
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

        List<String> imageUrls = story.getImages().stream()
                .sorted(Comparator.comparingLong(StoryImage::getOrderIndex))
                .map(StoryImage::getImageKey)
                .toList();

        return new StoryResponse(story, storeId);
    }

    @Transactional(readOnly = true)
    public StoriesDetailResponse getPagedStoryDetails(String kakaoId, int size) {
        List<Story> stories = storyRepository
                .findAllByStoreKakaoIdOrderByCreatedAtDesc(kakaoId, PageRequest.of(PAGE_START_NUMBER, size))
                .getContent();

        List<StoriesDetailResponse.StoryDetailResponse> responses = stories.stream()
                .map(story -> new StoriesDetailResponse.StoryDetailResponse(
                        story,
                        storyImageRepository.findAllByStory_IdOrderByOrderIndexAsc(story.getId())
                                .stream()
                                .map(StoryImage::getImageKey)
                                .toList()
                ))
                .toList(); // TODO: N+1 문제 해결
        return new StoriesDetailResponse(responses);
    }
}
