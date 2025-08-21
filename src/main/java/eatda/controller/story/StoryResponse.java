package eatda.controller.story;

import eatda.domain.story.Story;
import java.util.Comparator;
import java.util.List;
import org.springframework.lang.Nullable;

public record StoryResponse(
        @Nullable Long storeId,
        String storeKakaoId,
        String category,
        String storeName,
        String storeDistrict,
        String storeNeighborhood,
        String description,
        List<StoryImageResponse> images,
        long memberId,
        String memberNickname
) {
    public StoryResponse(Story story, Long storeId, String cdnBaseUrl) {
        this(
                storeId,
                story.getStoreKakaoId(),
                story.getStoreCategory().getCategoryName(),
                story.getStoreName(),
                story.getAddressDistrict(),
                story.getAddressNeighborhood(),
                story.getDescription(),
                story.getImages().stream()
                        .map(img -> new StoryImageResponse(img, cdnBaseUrl))
                        .sorted(Comparator.comparingLong(StoryImageResponse::orderIndex))
                        .toList(),
                story.getMember().getId(),
                story.getMember().getNickname()
        );
    }
}
