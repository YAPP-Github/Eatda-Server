package eatda.service.cheer;

import eatda.controller.cheer.CheerImageResponse;
import eatda.controller.cheer.CheerInStoreResponse;
import eatda.controller.cheer.CheerPreviewResponse;
import eatda.controller.cheer.CheerRegisterRequest;
import eatda.controller.cheer.CheerSearchParameters;
import eatda.controller.cheer.CheersInStoreResponse;
import eatda.controller.cheer.CheersResponse;
import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerImage;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.domain.store.StoreSearchResult;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.facade.CheerCreationResult;
import eatda.repository.cheer.CheerRepository;
import eatda.repository.member.MemberRepository;
import eatda.repository.store.StoreRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheerService {

    private static final int MAX_CHEER_SIZE = 10_000;
    private static final String SORTED_PROPERTIES = "createdAt";
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final CheerRepository cheerRepository;

    @Value("${cdn.base-url}")
    private String cdnBaseUrl;

    @Transactional
    public CheerCreationResult createCheer(CheerRegisterRequest request,
                                           StoreSearchResult result,
                                           long memberId
    ) {
        Member member = memberRepository.getById(memberId);
        validateRegisterCheer(member, request.storeKakaoId());

        Store store = storeRepository.findByKakaoId(result.kakaoId())
                .orElseGet(() -> storeRepository.save(result.toStore())); // TODO 상점 조회/저장 동시성 이슈 해결
        Cheer cheer = new Cheer(member, store, request.description());
        cheer.setCheerTags(request.tags());
        return new CheerCreationResult(cheerRepository.save(cheer), store);
    }

    private void validateRegisterCheer(Member member, String storeKakaoId) {
        if (cheerRepository.countByMember(member) >= MAX_CHEER_SIZE) {
            throw new BusinessException(BusinessErrorCode.FULL_CHEER_SIZE_PER_MEMBER);
        }
        if (cheerRepository.existsByMemberAndStoreKakaoId(member, storeKakaoId)) {
            throw new BusinessException(BusinessErrorCode.ALREADY_CHEERED);
        }
    }

    @Transactional
    public Cheer saveCheerImages(Long cheerId,
                                 List<CheerRegisterRequest.UploadedImageDetail> sortedImages,
                                 List<String> permanentKeys) {

        Cheer persistentCheer = cheerRepository.findById(cheerId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.CHEER_NOT_FOUND));

        IntStream.range(0, sortedImages.size())
                .forEach(i -> {
                    var detail = sortedImages.get(i);
                    CheerImage cheerImage = new CheerImage(
                            persistentCheer,
                            permanentKeys.get(i),
                            detail.orderIndex(),
                            detail.contentType(),
                            detail.fileSize()
                    );
                    persistentCheer.addImage(cheerImage);
                });

        return persistentCheer;
    }

    @Transactional(readOnly = true)
    public CheersResponse getCheers(CheerSearchParameters parameters) {
        Page<Cheer> cheerPage = cheerRepository.findAllByConditions(
                parameters.getCategory(),
                parameters.getCheerTagNames(),
                parameters.getDistricts(),
                PageRequest.of(parameters.getPage(), parameters.getSize(),
                        Sort.by(Direction.DESC, SORTED_PROPERTIES))
        );

        List<Cheer> cheers = cheerPage.getContent();
        return toCheersResponse(cheers);
    }

    private CheersResponse toCheersResponse(List<Cheer> cheers) {
        return new CheersResponse(cheers.stream()
                .map(cheer -> {
                    Store store = cheer.getStore();
                    return new CheerPreviewResponse(cheer,
                            cheer.getImages().stream()
                                    .map(img -> new CheerImageResponse(img, cdnBaseUrl))
                                    .sorted(Comparator.comparingLong(CheerImageResponse::orderIndex))
                                    .toList());
                })
                .toList());
    }

    @Transactional(readOnly = true)
    public CheersInStoreResponse getCheersByStoreId(Long storeId, int page, int size) {
        Store store = storeRepository.getById(storeId);
        Page<Cheer> cheersPage = cheerRepository.findAllByStoreOrderByCreatedAtDesc(store, PageRequest.of(page, size));

        List<CheerInStoreResponse> cheersResponse = cheersPage.getContent().stream()
                .map(CheerInStoreResponse::new)
                .toList();

        return new CheersInStoreResponse(cheersResponse);
    }

    @Transactional
    public void deleteCheer(Long cheerId) {
        cheerRepository.deleteById(cheerId);
    }
}
