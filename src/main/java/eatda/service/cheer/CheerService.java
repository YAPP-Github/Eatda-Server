package eatda.service.cheer;

import eatda.controller.cheer.CheerInStoreResponse;
import eatda.controller.cheer.CheerPreviewResponse;
import eatda.controller.cheer.CheerRegisterRequest;
import eatda.controller.cheer.CheerResponse;
import eatda.controller.cheer.CheersInStoreResponse;
import eatda.controller.cheer.CheersResponse;
import eatda.domain.ImageKey;
import eatda.domain.cheer.Cheer;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.domain.store.StoreSearchResult;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.repository.cheer.CheerRepository;
import eatda.repository.cheer.CheerTagRepository;
import eatda.repository.member.MemberRepository;
import eatda.repository.store.StoreRepository;
import eatda.storage.image.ImageStorage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheerService {

    private static final int MAX_CHEER_SIZE = 3;

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final CheerRepository cheerRepository;
    private final CheerTagRepository cheerTagRepository;
    private final ImageStorage imageStorage;

    @Transactional
    public CheerResponse registerCheer(CheerRegisterRequest request,
                                       StoreSearchResult result,
                                       ImageKey imageKey,
                                       long memberId) {
        Member member = memberRepository.getById(memberId);
        validateRegisterCheer(member, request.storeKakaoId());

        Store store = storeRepository.findByKakaoId(result.kakaoId())
                .orElseGet(() -> storeRepository.save(result.toStore())); // TODO 상점 조회/저장 동시성 이슈 해결
        Cheer cheer = new Cheer(member, store, request.description(), imageKey);
        cheer.setCheerTags(request.tags());
        Cheer savedCheer = cheerRepository.save(cheer);
        return new CheerResponse(savedCheer, store, imageStorage.getPreSignedUrl(imageKey));
    }

    private void validateRegisterCheer(Member member, String storeKakaoId) {
        if (cheerRepository.countByMember(member) >= MAX_CHEER_SIZE) {
            throw new BusinessException(BusinessErrorCode.FULL_CHEER_SIZE_PER_MEMBER);
        }
        if (cheerRepository.existsByMemberAndStoreKakaoId(member, storeKakaoId)) {
            throw new BusinessException(BusinessErrorCode.ALREADY_CHEERED);
        }
    }

    @Transactional(readOnly = true)
    public CheersResponse getCheers(int page, int size) {
        List<Cheer> cheers = cheerRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
        return toCheersResponse(cheers);
    }

    private CheersResponse toCheersResponse(List<Cheer> cheers) {
        return new CheersResponse(cheers.stream()
                .map(cheer -> new CheerPreviewResponse(cheer, cheer.getStore(),
                        imageStorage.getPreSignedUrl(cheer.getImageKey())))
                .toList());
    }

    @Transactional(readOnly = true)
    public CheersInStoreResponse getCheersByStoreId(Long storeId, int page, int size) {
        Store store = storeRepository.getById(storeId);
        List<Cheer> cheers = cheerRepository.findAllByStoreOrderByCreatedAtDesc(store, PageRequest.of(page, size));

        List<CheerInStoreResponse> cheersResponse = cheers.stream()
                .map(CheerInStoreResponse::new)
                .toList(); // TODO N+1 문제 해결
        return new CheersInStoreResponse(cheersResponse);
    }
}
