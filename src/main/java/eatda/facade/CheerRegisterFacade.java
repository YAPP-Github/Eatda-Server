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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheerRegisterFacade {

    @Value("${cdn.base-url}")
    private String cdnBaseUrl;
    private final CheerService cheerService;
    private final FileClient fileClient;

    public CheerResponse registerCheer(CheerRegisterRequest request,
                                       StoreSearchResult result,
                                       long memberId,
                                       ImageDomain domain
    ) {
        CheerCreationResult creationResult = cheerService.createCheer(request, result, memberId);
        Cheer cheer = creationResult.cheer();

        try {
            List<CheerRegisterRequest.UploadedImageDetail> sortedImages = sortImages(request.images());
            List<String> permanentKeys = moveImages(domain, cheer.getId(), sortedImages);
            cheer = cheerService.saveCheerImages(cheer.getId(), sortedImages, permanentKeys);
        } catch (Exception e) {
            cheerService.deleteCheer(cheer.getId());
            throw e;
        }

        return new CheerResponse(cheer, creationResult.store(), cdnBaseUrl);
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
        List<String> tempKeys = sortedImages.stream()
                .map(CheerRegisterRequest.UploadedImageDetail::imageKey)
                .toList();
        return fileClient.moveTempFilesToPermanent(domain.getName(), cheerId, tempKeys);
    }
}
