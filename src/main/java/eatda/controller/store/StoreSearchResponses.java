package eatda.controller.store;

import eatda.client.map.MapClientStoreSearchResult;
import java.util.List;

public record StoreSearchResponses(List<StoreSearchResponse> stores) {

    public static StoreSearchResponses from(List<MapClientStoreSearchResult> searchResults) {
        List<StoreSearchResponse> storeResponses = searchResults.stream()
                .map(StoreSearchResponse::new)
                .toList();
        return new StoreSearchResponses(storeResponses);
    }
}
