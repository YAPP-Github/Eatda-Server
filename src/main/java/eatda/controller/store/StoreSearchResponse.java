package eatda.controller.store;

import eatda.client.map.MapClientStoreSearchResult;

public record StoreSearchResponse(
        String kakaoId,
        String name,
        String address
) {

    public StoreSearchResponse(MapClientStoreSearchResult searchResult) {
        this(
                searchResult.kakaoId(),
                searchResult.name(),
                searchResult.lotNumberAddress()
        );
    }
}
