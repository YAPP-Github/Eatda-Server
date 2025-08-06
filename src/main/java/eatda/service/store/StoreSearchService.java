package eatda.service.store;

import eatda.client.map.MapClient;
import eatda.client.map.MapClientStoreSearchResult;
import eatda.domain.store.StoreSearchFilter;
import eatda.domain.store.StoreSearchResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreSearchService {

    private final MapClient mapClient;
    private final StoreSearchFilter storeSearchFilter;

    public StoreSearchResult searchStoreByKakaoId(String name, String kakaoId) {
        List<MapClientStoreSearchResult> searchResults = mapClient.searchStores(name);
        return storeSearchFilter.filterStoreByKakaoId(searchResults, kakaoId);
    }

    public List<StoreSearchResult> searchStores(String name) {
        List<MapClientStoreSearchResult> searchResults = mapClient.searchStores(name);
        return storeSearchFilter.filterSearchedStores(searchResults);
    }
}
