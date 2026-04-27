package com.inventory.inventory.dealer.api;

import com.inventory.inventory.dealer.application.DealerService;
import com.inventory.inventory.dealer.dto.DealerDto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/dealers")
@RequiredArgsConstructor
public class DealerController {

    private final DealerService dealerService;

    @PostMapping
    public ResponseEntity<DealerResponse> createDealer(
            @Valid @RequestBody CreateDealerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dealerService.createDealer(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DealerResponse> getDealer(@PathVariable UUID id) {
        return ResponseEntity.ok(dealerService.getDealer(id));
    }

    @GetMapping
    public ResponseEntity<Page<DealerResponse>> listDealers(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(dealerService.listDealers(pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DealerResponse> updateDealer(
            @PathVariable UUID id,
            @RequestBody UpdateDealerRequest request) {
        return ResponseEntity.ok(dealerService.updateDealer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDealer(@PathVariable UUID id) {
        dealerService.deleteDealer(id);
        return ResponseEntity.noContent().build();
    }
}
