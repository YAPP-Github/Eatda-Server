package eatda.facade;

import eatda.client.file.FileClient;
import eatda.controller.cheer.CheerRegisterRequest;
import eatda.controller.cheer.CheerResponse;
import eatda.domain.ImageDomain;
import eatda.domain.cheer.Cheer;
import eatda.domain.store.StoreSearchResult;
import eatda.service.cheer.CheerService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;

@Component
@RequiredArgsConstructor
public class CheerRegisterFacade {

    private final CheerService cheerService;
    private final FileClient fileClient;

    public CheerResponse registerCheer(CheerRegisterRequest request,
                                       StoreSearchResult result,
                                       long memberId,
                                       ImageDomain domain
    ) {
        CheerCreationResult creationResult = cheerService.createCheer(request, result, memberId);
        Cheer cheer = creationResult.cheer();

        if (request.images() == null || request.images().isEmpty()) {
            return cheerService.getCheerResponse(cheer.getId());
        }

        try {
            List<CheerRegisterRequest.UploadedImageDetail> sortedImages = sortImages(request.images());
            List<String> permanentKeys = moveImages(domain, cheer.getId(), sortedImages);
            cheerService.saveCheerImages(cheer.getId(), sortedImages, permanentKeys);
        } catch (SdkException sdkException) {
            cheerService.deleteCheer(cheer.getId());
            throw sdkException;
        }

        return cheerService.getCheerResponse(cheer.getId());
    }

    private List<CheerRegisterRequest.UploadedImageDetail> sortImages(
            List<CheerRegisterRequest.UploadedImageDetail> images) {
        return images.stream()
                .sorted(Comparator.comparingLong(CheerRegisterRequest.UploadedImageDetail::orderIndex))
                .toList();
    }

    private List<String> moveImages(ImageDomain domain,
                                    long cheerId,
                                    List<CheerRegisterRequest.UploadedImageDetail> sortedImages) {
        if (sortedImages.isEmpty()) {
            return List.of();
        }

        List<String> tempKeys = sortedImages.stream()
                .map(CheerRegisterRequest.UploadedImageDetail::imageKey)
                .toList();
        return fileClient.moveTempFilesToPermanent(domain.getName(), cheerId, tempKeys);
    }
}
