package eatda.service.store;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import eatda.controller.store.ImagesResponse;
import eatda.controller.store.StoreInMemberResponse;
import eatda.controller.store.StorePreviewResponse;
import eatda.controller.store.StoreResponse;
import eatda.controller.store.StoresInMemberResponse;
import eatda.controller.store.StoresResponse;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.repository.cheer.CheerRepository;
import eatda.repository.store.StoreRepository;
import eatda.storage.image.ImageStorage;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
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
    public StoresResponse getStores(int page, int size, @Nullable String category) {
        return findStores(page, size, category)
                .stream()
                .map(store -> new StorePreviewResponse(store, getStoreImageUrl(store).orElse(null)))
                .collect(collectingAndThen(toList(), StoresResponse::new));
    }

    private List<Store> findStores(int page, int size, @Nullable String category) {
        if (category == null || category.isBlank()) {
            return storeRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
        }
        return storeRepository.findAllByCategoryOrderByCreatedAtDesc(
                StoreCategory.from(category), PageRequest.of(page, size));
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
                .toList();
        return new StoresInMemberResponse(responses);
    }
}
