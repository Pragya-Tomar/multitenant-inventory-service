package com.inventory.inventory.vehicle.application;

import com.inventory.inventory.dealer.domain.Dealer;
import com.inventory.inventory.dealer.repository.DealerRepository;
import com.inventory.inventory.vehicle.domain.Vehicle;
import com.inventory.inventory.vehicle.dto.VehicleDto.*;
import com.inventory.inventory.vehicle.repository.VehicleRepository;
import com.inventory.shared.context.TenantContext;
import com.inventory.shared.exception.Exceptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DealerRepository dealerRepository;

    public VehicleService(VehicleRepository vehicleRepository, DealerRepository dealerRepository) {
        this.vehicleRepository = vehicleRepository;
        this.dealerRepository = dealerRepository;
    }

    @Transactional
    public VehicleResponse createVehicle(CreateVehicleRequest req) {
        String tenantId = TenantContext.getTenantId();
        Dealer dealer = dealerRepository.findByIdAndTenantId(req.dealerId(), tenantId)
                .orElseThrow(() -> new Exceptions.ResourceNotFoundException(
                        "Dealer not found in this tenant: " + req.dealerId()));
        Vehicle vehicle = Vehicle.builder()
                .tenantId(tenantId).dealer(dealer)
                .model(req.model()).price(req.price()).status(req.status())
                .build();
        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    public VehicleResponse getVehicle(UUID id) {
        return VehicleResponse.from(findTenantScoped(id));
    }

    public Page<VehicleResponse> listVehicles(VehicleFilter filter, Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        Specification<Vehicle> spec = VehicleRepository.hasTenant(tenantId);
        if (StringUtils.hasText(filter.model())) spec = spec.and(VehicleRepository.hasModel(filter.model()));
        if (filter.status() != null) spec = spec.and(VehicleRepository.hasStatus(filter.status()));
        if (filter.priceMin() != null) spec = spec.and(VehicleRepository.hasPriceMin(filter.priceMin()));
        if (filter.priceMax() != null) spec = spec.and(VehicleRepository.hasPriceMax(filter.priceMax()));
        if ("PREMIUM".equalsIgnoreCase(filter.subscription()))
            spec = spec.and(VehicleRepository.hasDealerSubscription(Dealer.SubscriptionType.PREMIUM));
        return vehicleRepository.findAll(spec, pageable).map(VehicleResponse::from);
    }

    @Transactional
    public VehicleResponse updateVehicle(UUID id, UpdateVehicleRequest req) {
        Vehicle vehicle = findTenantScoped(id);
        if (StringUtils.hasText(req.model())) vehicle.setModel(req.model());
        if (req.price() != null) vehicle.setPrice(req.price());
        if (req.status() != null) vehicle.setStatus(req.status());
        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(UUID id) {
        vehicleRepository.delete(findTenantScoped(id));
    }

    private Vehicle findTenantScoped(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new Exceptions.ResourceNotFoundException("Vehicle not found: " + id));
        if (!vehicle.getTenantId().equals(TenantContext.getTenantId())) {
            throw new Exceptions.TenantAccessDeniedException();
        }
        return vehicle;
    }
}
