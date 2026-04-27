package com.inventory.inventory.vehicle.dto;

import com.inventory.inventory.vehicle.domain.Vehicle;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class VehicleDto {

    private VehicleDto() {}

    public record CreateVehicleRequest(
            @NotNull(message = "dealerId is required")
            UUID dealerId,

            @NotBlank(message = "model is required")
            String model,

            @NotNull(message = "price is required")
            @DecimalMin(value = "0.01", message = "price must be positive")
            BigDecimal price,

            @NotNull(message = "status is required (AVAILABLE or SOLD)")
            Vehicle.VehicleStatus status
    ) {}

    public record UpdateVehicleRequest(
            String model,
            BigDecimal price,
            Vehicle.VehicleStatus status
    ) {}

    public record VehicleFilter(
            String model,
            Vehicle.VehicleStatus status,
            BigDecimal priceMin,
            BigDecimal priceMax,
            String subscription
    ) {}

    public record VehicleResponse(
            UUID id,
            String tenantId,
            UUID dealerId,
            String dealerName,
            String model,
            BigDecimal price,
            Vehicle.VehicleStatus status,
            String dealerSubscription,
            Instant createdAt,
            Instant updatedAt
    ) {
        public static VehicleResponse from(Vehicle v) {
            return new VehicleResponse(
                    v.getId(),
                    v.getTenantId(),
                    v.getDealer().getId(),
                    v.getDealer().getName(),
                    v.getModel(),
                    v.getPrice(),
                    v.getStatus(),
                    v.getDealer().getSubscriptionType().name(),
                    v.getCreatedAt(),
                    v.getUpdatedAt()
            );
        }
    }
}
