package eatda.controller.cheer;

import eatda.controller.web.auth.LoginMember;
import eatda.domain.ImageDomain;
import eatda.domain.store.StoreSearchResult;
import eatda.service.cheer.CheerService;
import eatda.service.store.StoreSearchService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class CheerController {

    private final CheerService cheerService;
    private final StoreSearchService storeSearchService;

    @PostMapping("/api/cheer")
    public ResponseEntity<CheerResponse> registerCheer(@RequestBody CheerRegisterRequest request,
                                                       LoginMember member) {
        StoreSearchResult searchResult = storeSearchService.searchStoreByKakaoId(
                request.storeName(), request.storeKakaoId());
        CheerResponse response = cheerService.registerCheer(request, searchResult, member.id(), ImageDomain.CHEER);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/api/cheer")
    public ResponseEntity<CheersResponse> getCheers(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                    @RequestParam(defaultValue = "5") @Min(1) @Max(50) int size) {
        CheersResponse response = cheerService.getCheers(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/shops/{storeId}/cheers")
    public ResponseEntity<CheersInStoreResponse> getCheersByStoreId(@PathVariable Long storeId,
                                                                    @RequestParam(defaultValue = "0") @Min(0) int page,
                                                                    @RequestParam(defaultValue = "5") @Min(1) @Max(50) int size) {
        CheersInStoreResponse response = cheerService.getCheersByStoreId(storeId, page, size);
        return ResponseEntity.ok(response);
    }
}
