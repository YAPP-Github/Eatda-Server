package eatda.service.cheer;

import eatda.client.file.FileClient;
import eatda.controller.cheer.CheerImageResponse;
import eatda.controller.cheer.CheerInStoreResponse;
import eatda.controller.cheer.CheerPreviewResponse;
import eatda.controller.cheer.CheerRegisterRequest;
import eatda.controller.cheer.CheerResponse;
import eatda.controller.cheer.CheerSearchParameters;
import eatda.controller.cheer.CheersInStoreResponse;
import eatda.controller.cheer.CheersResponse;
import eatda.domain.ImageDomain;
import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerImage;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.domain.store.StoreSearchResult;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
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

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final CheerRepository cheerRepository;
    private final FileClient fileClient;

    @Value("${cdn.base-url}")
    private String cdnBaseUrl;

    @Transactional
    public CheerResponse registerCheer(CheerRegisterRequest request,
                                       StoreSearchResult result,
                                       long memberId,
                                       ImageDomain domain
    ) {
        Member member = memberRepository.getById(memberId);
        validateRegisterCheer(member, request.storeKakaoId());

        Store store = storeRepository.findByKakaoId(result.kakaoId())
                .orElseGet(() -> storeRepository.save(result.toStore())); // TODO 상점 조회/저장 동시성 이슈 해결
        Cheer cheer = new Cheer(member, store, request.description());
        cheer.setCheerTags(request.tags());
        Cheer savedCheer = cheerRepository.save(cheer);

        // TODO 트랜잭션 범위 축소
        List<CheerRegisterRequest.UploadedImageDetail> sortedImages = sortImages(request.images());
        List<String> permanentKeys = moveImages(domain, cheer.getId(), sortedImages);

        saveCheerImages(cheer, sortedImages, permanentKeys);

        return new CheerResponse(savedCheer, store, cdnBaseUrl);
    }

    private void validateRegisterCheer(Member member, String storeKakaoId) {
        if (cheerRepository.countByMember(member) >= MAX_CHEER_SIZE) {
            throw new BusinessException(BusinessErrorCode.FULL_CHEER_SIZE_PER_MEMBER);
        }
        if (cheerRepository.existsByMemberAndStoreKakaoId(member, storeKakaoId)) {
            throw new BusinessException(BusinessErrorCode.ALREADY_CHEERED);
        }
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

    private void saveCheerImages(Cheer cheer,
                                 List<CheerRegisterRequest.UploadedImageDetail> sortedImages,
                                 List<String> permanentKeys) {
        IntStream.range(0, sortedImages.size())
                .forEach(i -> {
                    var detail = sortedImages.get(i);
                    CheerImage cheerImage = new CheerImage(
                            cheer,
                            permanentKeys.get(i),
                            detail.orderIndex(),
                            detail.contentType(),
                            detail.fileSize()
                    );
                    cheer.addImage(cheerImage); // 여기서 양방향 동기화
                });

        cheerRepository.save(cheer);
    }

    @Transactional(readOnly = true)
    public CheersResponse getCheers(CheerSearchParameters parameters) {
        Page<Cheer> cheerPage = cheerRepository.findAllByConditions(
                parameters.getCategory(),
                parameters.getCheerTagNames(),
                parameters.getDistricts(),
                PageRequest.of(parameters.getPage(), parameters.getSize(),
                        Sort.by(Direction.DESC, "createdAt"))
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
}
