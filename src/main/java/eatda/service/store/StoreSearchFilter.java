package eatda.service.store;

import eatda.client.map.StoreSearchResult;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StoreSearchFilter {

    public List<StoreSearchResult> filterSearchedStores(List<StoreSearchResult> searchResults) {
        return searchResults.stream()
                .filter(this::isValidStore)
                .toList();
    }

    private boolean isValidStore(StoreSearchResult store) {
        return store.isFoodStore() && store.isInSeoul();
    }
}
