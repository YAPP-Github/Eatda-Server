package eatda.domain.store;

import eatda.client.map.MapClientStoreSearchResult;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StoreSearchFilter {

    public List<StoreSearchResult> filterSearchedStores(List<MapClientStoreSearchResult> searchResults) {
        return searchResults.stream()
                .filter(this::isValidStore)
                .map(MapClientStoreSearchResult::toDomain)
                .toList();
    }

    public StoreSearchResult filterStoreByKakaoId(List<MapClientStoreSearchResult> searchResults, String kakaoId) {
        return searchResults.stream()
                .filter(store -> store.kakaoId().equals(kakaoId))
                .filter(this::isValidStore)
                .findFirst()
                .map(MapClientStoreSearchResult::toDomain)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.STORE_NOT_FOUND));
    }

    private boolean isValidStore(MapClientStoreSearchResult store) {
        return store.isFoodStore() && store.isInSeoul();
    }
}
