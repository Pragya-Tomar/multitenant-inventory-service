package com.inventory.inventory.vehicle.repository;

import com.inventory.inventory.dealer.domain.Dealer;
import com.inventory.inventory.vehicle.domain.Vehicle;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository
        extends JpaRepository<Vehicle, UUID>, JpaSpecificationExecutor<Vehicle> {

    Optional<Vehicle> findByIdAndTenantId(UUID id, String tenantId);

    static Specification<Vehicle> hasTenant(String tenantId) {
        return (root, query, cb) -> cb.equal(root.get("tenantId"), tenantId);
    }

    static Specification<Vehicle> hasModel(String model) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("model")), "%" + model.toLowerCase() + "%");
    }

    static Specification<Vehicle> hasStatus(Vehicle.VehicleStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    static Specification<Vehicle> hasPriceMin(BigDecimal min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    static Specification<Vehicle> hasPriceMax(BigDecimal max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), max);
    }

    static Specification<Vehicle> hasDealerSubscription(Dealer.SubscriptionType type) {
        return (root, query, cb) -> {
            var dealer = root.join("dealer");
            return cb.equal(dealer.get("subscriptionType"), type);
        };
    }
}
