package eatda.service.store;

import eatda.client.map.MapClient;
import eatda.client.map.StoreSearchResult;
import eatda.controller.store.CheerPreviewResponse;
import eatda.controller.store.CheerRegisterRequest;
import eatda.controller.store.CheerResponse;
import eatda.controller.store.CheersResponse;
import eatda.domain.member.Member;
import eatda.domain.store.Cheer;
import eatda.domain.store.Store;
import eatda.repository.member.MemberRepository;
import eatda.repository.store.CheerRepository;
import eatda.repository.store.StoreRepository;
import eatda.service.common.ImageDomain;
import eatda.service.common.ImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CheerService {

    private final MapClient mapClient;
    private final StoreSearchFilter storeSearchFilter;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final CheerRepository cheerRepository;
    private final ImageService imageService;

    @Transactional
    public CheerResponse registerCheer(CheerRegisterRequest request, MultipartFile image, long memberId) {
        List<StoreSearchResult> searchResults = mapClient.searchShops(request.storeName());
        StoreSearchResult result = storeSearchFilter.filterStoreByKakaoId(searchResults, request.kakaoId());
        String imageKey = imageService.upload(image, ImageDomain.CHEER);

        Member member = memberRepository.getById(memberId);
        Store store = storeRepository.findByKakaoId(result.kakaoId())
                .orElseGet(() -> storeRepository.save(result.toStore()));
        Cheer cheer = cheerRepository.save(new Cheer(member, store, request.description(), imageKey));
        return new CheerResponse(cheer, imageService.getPresignedUrl(imageKey), store);
    }

    @Transactional(readOnly = true)
    public CheersResponse getCheers(int size) {
        List<Cheer> cheers = cheerRepository.findAllByOrderByCreatedAtDesc(Pageable.ofSize(size));
        return toCheersResponse(cheers);
    }

    private CheersResponse toCheersResponse(List<Cheer> cheers) {
        return new CheersResponse(cheers.stream()
                .map(cheer -> new CheerPreviewResponse(cheer, cheer.getStore(),
                        imageService.getPresignedUrl(cheer.getImageKey())))
                .toList());
    }
}
