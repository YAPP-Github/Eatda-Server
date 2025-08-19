package eatda.service.store;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import eatda.controller.store.ImagesResponse;
import eatda.controller.store.StoreInMemberResponse;
import eatda.controller.store.StorePreviewResponse;
import eatda.controller.store.StoreResponse;
import eatda.controller.store.StoresInMemberResponse;
import eatda.controller.store.StoresResponse;
import eatda.controller.store.TagsResponse;
import eatda.domain.cheer.CheerImage;
import eatda.domain.cheer.CheerTag;
import eatda.domain.store.Store;
import eatda.domain.store.StoreCategory;
import eatda.repository.cheer.CheerImageRepository;
import eatda.repository.cheer.CheerRepository;
import eatda.repository.cheer.CheerTagRepository;
import eatda.repository.store.StoreRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final CheerRepository cheerRepository;
    private final CheerTagRepository cheerTagRepository;
    private final CheerImageRepository cheerImageRepository;

    @Value("${cdn.base-url}")
    private String cdnBaseUrl;

    public StoreResponse getStore(long storeId) {
        Store store = storeRepository.getById(storeId);
        return new StoreResponse(store);
    }

    // TODO : N+1 문제 해결
    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public TagsResponse getStoreTags(long storeId) {
        Store store = storeRepository.getById(storeId);
        List<CheerTag> cheerTags = cheerTagRepository.findAllByCheerStore(store);
        return TagsResponse.from(cheerTags);
    }

    @Transactional(readOnly = true)
    public ImagesResponse getStoreImages(long storeId) {
        Store store = storeRepository.getById(storeId);
        List<String> urls = cheerImageRepository.findAllByCheer_StoreOrderByOrderIndexAsc(store)
                .stream()
                .map(img -> "https://" + cdnBaseUrl + "/" + img.getImageKey())
                .toList();
        return new ImagesResponse(urls);
    }

    private Optional<String> getStoreImageUrl(Store store) {
        return cheerImageRepository.findFirstByCheer_Store_IdOrderByCreatedAtDesc(store.getId())
                .map(CheerImage::getImageKey)
                .map(imageKey -> "https://" + cdnBaseUrl + "/" + imageKey);
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
