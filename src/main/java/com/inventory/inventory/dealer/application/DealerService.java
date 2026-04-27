package com.inventory.inventory.dealer.application;

import com.inventory.inventory.dealer.domain.Dealer;
import com.inventory.inventory.dealer.dto.DealerDto.*;
import com.inventory.inventory.dealer.repository.DealerRepository;
import com.inventory.shared.context.TenantContext;
import com.inventory.shared.exception.Exceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DealerService {

    private final DealerRepository dealerRepository;

    @Transactional
    public DealerResponse createDealer(CreateDealerRequest req) {
        String tenantId = TenantContext.getTenantId();

        if (dealerRepository.existsByEmailAndTenantId(req.email(), tenantId)) {
            throw new Exceptions.InvalidRequestException(
                    "A dealer with email " + req.email() + " already exists in this tenant");
        }

        Dealer dealer = Dealer.builder()
                .tenantId(tenantId)
                .name(req.name())
                .email(req.email())
                .subscriptionType(req.subscriptionType())
                .build();

        return DealerResponse.from(dealerRepository.save(dealer));
    }

    public DealerResponse getDealer(UUID id) {
        return DealerResponse.from(findTenantScoped(id));
    }

    public Page<DealerResponse> listDealers(Pageable pageable) {
        return dealerRepository.findAllByTenantId(TenantContext.getTenantId(), pageable)
                .map(DealerResponse::from);
    }

    @Transactional
    public DealerResponse updateDealer(UUID id, UpdateDealerRequest req) {
        Dealer dealer = findTenantScoped(id);

        if (StringUtils.hasText(req.name())) {
            dealer.setName(req.name());
        }
        if (StringUtils.hasText(req.email())) {
            dealer.setEmail(req.email());
        }
        if (req.subscriptionType() != null) {
            dealer.setSubscriptionType(req.subscriptionType());
        }

        return DealerResponse.from(dealerRepository.save(dealer));
    }

    @Transactional
    public void deleteDealer(UUID id) {
        Dealer dealer = findTenantScoped(id);
        dealerRepository.delete(dealer);
    }

    public Map<String, Long> countBySubscriptionGlobal() {
        return dealerRepository.countBySubscriptionTypeGlobal()
                .stream()
                .collect(Collectors.toMap(
                        row -> ((Dealer.SubscriptionType) row[0]).name(),
                        row -> (Long) row[1]
                ));
    }

    private Dealer findTenantScoped(UUID id) {
        Dealer dealer = dealerRepository.findById(id)
                .orElseThrow(() -> new Exceptions.ResourceNotFoundException(
                        "Dealer not found: " + id));

        if (!dealer.getTenantId().equals(TenantContext.getTenantId())) {
            throw new Exceptions.TenantAccessDeniedException();
        }

        return dealer;
    }
}
