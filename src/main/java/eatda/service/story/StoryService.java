package eatda.service.story;

import eatda.client.file.FileClient;
import eatda.controller.story.StoriesDetailResponse;
import eatda.controller.story.StoriesResponse;
import eatda.controller.story.StoryImageResponse;
import eatda.controller.story.StoryRegisterRequest;
import eatda.controller.story.StoryRegisterResponse;
import eatda.controller.story.StoryResponse;
import eatda.domain.ImageDomain;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Value("${cdn.base-url}")
    private String cdnBaseUrl;

    @Transactional
    public StoryRegisterResponse registerStory(StoryRegisterRequest request,
                                               StoreSearchResult result,
                                               ImageDomain domain,
                                               long memberId) {
        Member member = memberRepository.getById(memberId);

        Story story = storyRepository.save(Story.builder()
                .member(member)
                .storeKakaoId(result.kakaoId())
                .storeName(result.name())
                .storeRoadAddress(result.roadAddress())
                .storeLotNumberAddress(result.lotNumberAddress())
                .storeCategory(result.category())
                .description(request.description())
                .build());

        // TODO 트랜잭션 범위 축소
        List<StoryRegisterRequest.UploadedImageDetail> sortedImages = sortImages(request.images());
        List<String> permanentKeys = moveImages(domain.getName(), story.getId(), sortedImages);

        saveStoryImages(story, sortedImages, permanentKeys);

        return new StoryRegisterResponse(story.getId());
    }

    private List<StoryRegisterRequest.UploadedImageDetail> sortImages(
            List<StoryRegisterRequest.UploadedImageDetail> images) {
        return images.stream()
                .sorted(Comparator.comparingLong(StoryRegisterRequest.UploadedImageDetail::orderIndex))
                .toList();
    }

    private List<String> moveImages(String domainName,
                                    long storyId,
                                    List<StoryRegisterRequest.UploadedImageDetail> sortedImages) {
        List<String> tempKeys = sortedImages.stream()
                .map(StoryRegisterRequest.UploadedImageDetail::imageKey)
                .toList();
        return fileClient.moveTempFilesToPermanent(domainName, storyId, tempKeys);
    }

    private void saveStoryImages(Story story,
                                 List<StoryRegisterRequest.UploadedImageDetail> sortedImages,
                                 List<String> permanentKeys) {
        IntStream.range(0, sortedImages.size())
                .forEach(i -> {
                    var detail = sortedImages.get(i);
                    StoryImage storyImage = new StoryImage(
                            story,
                            permanentKeys.get(i),
                            detail.orderIndex(),
                            detail.contentType(),
                            detail.fileSize()
                    );
                    story.addImage(storyImage);
                });

        storyRepository.save(story);
    }

    @Transactional(readOnly = true)
    public StoriesResponse getPagedStoryPreviews(int size) {
        Page<Story> page = storyRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(PAGE_START_NUMBER, size));
        return toStoriesResponse(page.getContent());
    }

    private StoriesResponse toStoriesResponse(List<Story> stories) {
        return new StoriesResponse(
                stories.stream()
                        .map(story -> new StoriesResponse.StoryPreview(
                                story.getId(),
                                story.getImages().stream()
                                        .map(img -> new StoryImageResponse(img, cdnBaseUrl))
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

        return new StoryResponse(story, storeId, cdnBaseUrl);
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
                                .map(img -> new StoryImageResponse(img, cdnBaseUrl))
                                .toList()
                ))
                .toList();

        return new StoriesDetailResponse(responses);
    }
}
