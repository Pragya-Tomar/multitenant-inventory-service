package com.inventory.inventory.admin.api;

import com.inventory.inventory.dealer.application.DealerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final DealerService dealerService;

    public AdminController(DealerService dealerService) {
        this.dealerService = dealerService;
    }

    @GetMapping("/dealers/countBySubscription")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    public ResponseEntity<Map<String, Long>> countBySubscription() {
        return ResponseEntity.ok(dealerService.countBySubscriptionGlobal());
    }
}
