package edu.polytech.caissatn.audit;


import io.micrometer.common.lang.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.empty();
    }
//    private final CurrentTenantResolver currentTenantResolver;
//
//    public AuditAwareImpl(CurrentTenantResolver currentTenantResolver) {
//        this.currentTenantResolver = currentTenantResolver;
//    }
//
//    @Override
//    @NonNull
//    public Optional<String> getCurrentAuditor() {
//        String userId = currentTenantResolver.getCurrentUserId();
//        return Optional.ofNullable(userId);
//    }
}
