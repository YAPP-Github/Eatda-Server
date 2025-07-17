package eatda.service.store;

import eatda.controller.store.CheerPreviewResponse;
import eatda.controller.store.CheersResponse;
import eatda.domain.store.Cheer;
import eatda.repository.store.CheerRepository;
import eatda.service.common.ImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheerService {

    private final ImageService imageService;
    private final CheerRepository cheerRepository;

    @Transactional(readOnly = true)
    public CheersResponse getCheers(int size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        List<Cheer> cheers = cheerRepository.findAllByOrderByCreatedAtDesc(pageRequest);
        return toCheersResponse(cheers);
    }

    private CheersResponse toCheersResponse(List<Cheer> cheers) {
        return new CheersResponse(cheers.stream()
                .map(cheer -> new CheerPreviewResponse(cheer, cheer.getStore(),
                        imageService.getPresignedUrl(cheer.getImageKey())))
                .toList());
    }
}
