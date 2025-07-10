package eatda.controller.store;

import eatda.controller.web.auth.LoginMember;
import eatda.service.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/api/shop/search")
    public ResponseEntity<StoreSearchResponses> searchStore(@RequestParam String query, LoginMember member) {
        StoreSearchResponses response = storeService.searchStores(query);
        return ResponseEntity.ok(response);
    }
}
