package eatda.controller.story;

import eatda.domain.store.StoreCategory;

public record FilteredSearchResult(
        String kakaoId,
        String name,
        String roadAddress,
        String lotNumberAddress,
        StoreCategory category
) {
}
