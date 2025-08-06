package eatda.controller.store;

import eatda.controller.web.auth.LoginMember;
import eatda.domain.store.StoreSearchResult;
import eatda.service.image.ImageService;
import eatda.service.store.StoreSearchService;
import eatda.service.store.StoreService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final StoreSearchService storeSearchService;
    private final ImageService imageService;

    @GetMapping("/api/shops/{storeId}/images")
    public ResponseEntity<ImagesResponse> getStoreImages(@PathVariable long storeId) {
        return ResponseEntity.ok(storeService.getStoreImages(storeId));
    }

    @GetMapping("/api/shops")
    public ResponseEntity<StoresResponse> getStores(@RequestParam @Min(1) @Max(50) int size,
                                                    @RequestParam(required = false) String category) {
        return ResponseEntity.ok(storeService.getStores(size, category));
    }

    @GetMapping("/api/shops/{storeId}")
    public ResponseEntity<StoreResponse> getStore(@PathVariable long storeId) {
        StoreResponse response = storeService.getStore(storeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/shop/search")
    public ResponseEntity<StoreSearchResponses> searchStore(@RequestParam String query, LoginMember member) {
        List<StoreSearchResult> storeSearchResults = storeSearchService.searchStores(query);
        StoreSearchResponses response = StoreSearchResponses.from(storeSearchResults);
        return ResponseEntity.ok(response);
    }
}
