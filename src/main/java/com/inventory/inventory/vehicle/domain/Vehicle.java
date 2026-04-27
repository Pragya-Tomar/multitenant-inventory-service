package com.inventory.inventory.vehicle.domain;

import com.inventory.inventory.dealer.domain.Dealer;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public enum VehicleStatus {
        AVAILABLE, SOLD
    }

    public Vehicle() {}

    public UUID getId() { return id; }
    public String getTenantId() { return tenantId; }
    public Dealer getDealer() { return dealer; }
    public String getModel() { return model; }
    public BigDecimal getPrice() { return price; }
    public VehicleStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setId(UUID id) { this.id = id; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public void setDealer(Dealer dealer) { this.dealer = dealer; }
    public void setModel(String model) { this.model = model; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStatus(VehicleStatus status) { this.status = status; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Vehicle v = new Vehicle();
        public Builder tenantId(String t) { v.tenantId = t; return this; }
        public Builder dealer(Dealer d) { v.dealer = d; return this; }
        public Builder model(String m) { v.model = m; return this; }
        public Builder price(BigDecimal p) { v.price = p; return this; }
        public Builder status(VehicleStatus s) { v.status = s; return this; }
        public Vehicle build() { return v; }
    }
}
