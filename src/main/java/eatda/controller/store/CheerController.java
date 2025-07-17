package eatda.controller.store;

import eatda.service.store.CheerService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CheerController {

    private final CheerService cheerService;

    @GetMapping("/api/cheer")
    public ResponseEntity<CheersResponse> getCheers(@RequestParam @Min(1) @Max(50) int size) {
        CheersResponse response = cheerService.getCheers(size);
        return ResponseEntity.ok(response);
    }
}
