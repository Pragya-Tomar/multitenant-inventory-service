package com.inventory.inventory.dealer.dto;

import com.inventory.inventory.dealer.domain.Dealer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public final class DealerDto {

    private DealerDto() {}

    public record CreateDealerRequest(
            @NotBlank(message = "Name is required")
            String name,

            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email,

            @NotNull(message = "subscriptionType is required (BASIC or PREMIUM)")
            Dealer.SubscriptionType subscriptionType
    ) {}

    public record UpdateDealerRequest(
            String name,
            String email,
            Dealer.SubscriptionType subscriptionType
    ) {}

    public record DealerResponse(
            UUID id,
            String tenantId,
            String name,
            String email,
            Dealer.SubscriptionType subscriptionType,
            Instant createdAt,
            Instant updatedAt
    ) {
        public static DealerResponse from(Dealer d) {
            return new DealerResponse(
                    d.getId(), d.getTenantId(), d.getName(),
                    d.getEmail(), d.getSubscriptionType(),
                    d.getCreatedAt(), d.getUpdatedAt()
            );
        }
    }
}
