package eatda.controller.store;

import eatda.domain.store.Store;

public record StoreInMemberResponse(
        long id,
        String name,
        String district,
        String neighborhood,
        long cheerCount
) {
    public StoreInMemberResponse(Store store, int cheerCount) {
        this(
                store.getId(),
                store.getName(),
                store.getAddressDistrict(),
                store.getAddressNeighborhood(),
                cheerCount
        );
    }
}
