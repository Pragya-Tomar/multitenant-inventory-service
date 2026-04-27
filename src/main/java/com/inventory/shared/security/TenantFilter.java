package com.inventory.shared.security;

import com.inventory.shared.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class TenantFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";
    public static final String ROLE_HEADER   = "X-Role";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || path.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String tenantId = request.getHeader(TENANT_HEADER);

        if (!StringUtils.hasText(tenantId)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\":\"Missing required header: " + TENANT_HEADER + "\"}"
            );
            return;
        }

        try {
            TenantContext.setTenantId(tenantId.trim());
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
