package com.inventory.inventory.dealer.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dealers",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "email"}))
public class Dealer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false)
    private SubscriptionType subscriptionType;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public enum SubscriptionType {
        BASIC, PREMIUM
    }

    public Dealer() {}

    public UUID getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public SubscriptionType getSubscriptionType() { return subscriptionType; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setId(UUID id) { this.id = id; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setSubscriptionType(SubscriptionType subscriptionType) { this.subscriptionType = subscriptionType; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Dealer d = new Dealer();
        public Builder tenantId(String t) { d.tenantId = t; return this; }
        public Builder name(String n) { d.name = n; return this; }
        public Builder email(String e) { d.email = e; return this; }
        public Builder subscriptionType(SubscriptionType s) { d.subscriptionType = s; return this; }
        public Dealer build() { return d; }
    }
}
