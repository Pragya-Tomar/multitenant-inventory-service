package com.inventory.inventory.admin.api;

import com.inventory.inventory.dealer.application.DealerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DealerService dealerService;

    @GetMapping("/dealers/countBySubscription")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    public ResponseEntity<Map<String, Long>> countBySubscription() {
        return ResponseEntity.ok(dealerService.countBySubscriptionGlobal());
    }
}
