package eatda.controller.store;

import eatda.client.map.StoreSearchResult;
import java.util.List;

public record StoreSearchResponses(List<StoreSearchResponse> stores) {

    public static StoreSearchResponses from(List<StoreSearchResult> searchResults) {
        List<StoreSearchResponse> storeResponses = searchResults.stream()
                .map(StoreSearchResponse::new)
                .toList();
        return new StoreSearchResponses(storeResponses);
    }
}
