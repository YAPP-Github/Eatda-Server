package eatda.service.store;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import eatda.controller.store.ImagesResponse;
import eatda.controller.store.StorePreviewResponse;
import eatda.controller.store.StoreResponse;
import eatda.controller.store.StoresResponse;
import eatda.domain.cheer.CheerImage;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.repository.cheer.CheerImageRepository;
import eatda.repository.cheer.CheerRepository;
import eatda.repository.store.StoreRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final CheerRepository cheerRepository;
    private final CheerImageRepository cheerImageRepository;

    @Value("${cdn.base-url}")
    private String cdnBaseUrl;

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
        List<String> urls = cheerImageRepository.findAllByCheer_Store_IdOrderByOrderIndexAsc(storeId)
                .stream()
                .map(img -> cdnBaseUrl + "/" + img.getImageKey())
                .toList();
        return new ImagesResponse(urls);
    }

    private Optional<String> getStoreImageUrl(Store store) {
        return cheerImageRepository.findFirstByCheer_Store_IdOrderByCreatedAtDesc(store.getId())
                .map(CheerImage::getImageKey);
    }
}
