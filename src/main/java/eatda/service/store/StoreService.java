package eatda.service.store;

import eatda.controller.store.ImagesResponse;
import eatda.controller.store.StoreInMemberResponse;
import eatda.controller.store.StorePreviewResponse;
import eatda.controller.store.StoreResponse;
import eatda.controller.store.StoreSearchParameters;
import eatda.controller.store.StoresInMemberResponse;
import eatda.controller.store.StoresResponse;
import eatda.domain.store.Store;
import eatda.repository.cheer.CheerRepository;
import eatda.repository.store.StoreRepository;
import eatda.storage.image.ImageStorage;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final CheerRepository cheerRepository;
    private final ImageStorage imageStorage;

    public StoreResponse getStore(long storeId) {
        Store store = storeRepository.getById(storeId);
        return new StoreResponse(store);
    }

    // TODO : N+1 문제 해결
    @Transactional(readOnly = true)
    public StoresResponse getStores(StoreSearchParameters parameters) {
        List<Store> stores = storeRepository.findAllByConditions(
                parameters.getCategory(),
                parameters.getCheerTagNames(),
                parameters.getDistricts(),
                PageRequest.of(parameters.getPage(), parameters.getSize(), Sort.by(Direction.DESC, "createdAt"))
        ).getContent();

        List<StorePreviewResponse> responses = stores.stream()
                .map(store -> new StorePreviewResponse(store, getStoreImageUrl(store).orElse(null)))
                .toList();
        return new StoresResponse(responses);
    }

    public ImagesResponse getStoreImages(long storeId) {
        Store store = storeRepository.getById(storeId);
        List<String> imageUrls = cheerRepository.findAllImageKey(store)
                .stream()
                .map(imageStorage::getPreSignedUrl)
                .toList();
        return new ImagesResponse(imageUrls);
    }

    private Optional<String> getStoreImageUrl(Store store) {
        return cheerRepository.findRecentImageKey(store)
                .map(imageStorage::getPreSignedUrl);
    }

    @Transactional(readOnly = true)
    public StoresInMemberResponse getStoresByCheeredMember(long memberId) {
        List<Store> stores = storeRepository.findAllByCheeredMemberId(memberId);
        List<StoreInMemberResponse> responses = stores.stream()
                .map(store -> new StoreInMemberResponse(store, cheerRepository.countByStore(store)))
                .toList(); // TODO : N+1 문제 해결 (특정 회원의 가게는 3명 제한이라 중요도 낮음)
        return new StoresInMemberResponse(responses);
    }
}
