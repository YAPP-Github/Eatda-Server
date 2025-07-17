package eatda.service.store;

import eatda.client.map.MapClient;
import eatda.client.map.StoreSearchResult;
import eatda.controller.store.StoreSearchResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final MapClient mapClient;
    private final StoreSearchFilter storeSearchFilter;

    public StoreSearchResponses searchStores(String query) {
        List<StoreSearchResult> searchResults = mapClient.searchShops(query);
        List<StoreSearchResult> filteredResults = storeSearchFilter.filterSearchedStores(searchResults);
        return StoreSearchResponses.from(filteredResults);
    }

    public List<StoreSearchResult> searchStoreResults(String query) {
        List<StoreSearchResult> searchResults = mapClient.searchShops(query);
        return storeSearchFilter.filterSearchedStores(searchResults);
    }
}
