package com.inventory.inventory.vehicle.api;

import com.inventory.inventory.vehicle.application.VehicleService;
import com.inventory.inventory.vehicle.domain.Vehicle;
import com.inventory.inventory.vehicle.dto.VehicleDto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.createVehicle(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.getVehicle(id));
    }

    @GetMapping
    public ResponseEntity<Page<VehicleResponse>> listVehicles(
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Vehicle.VehicleStatus status,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) String subscription,
            @PageableDefault(size = 20, sort = "model") Pageable pageable) {
        return ResponseEntity.ok(vehicleService.listVehicles(
                new VehicleFilter(model, status, priceMin, priceMax, subscription), pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(@PathVariable UUID id,
            @RequestBody UpdateVehicleRequest request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable UUID id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
