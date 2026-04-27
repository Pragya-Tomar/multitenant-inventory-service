package com.inventory.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   RoleHeaderAuthFilter roleFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**", "/h2-console/**").permitAll()
                .requestMatchers("/admin/**").hasRole("GLOBAL_ADMIN")
                .anyRequest().hasAnyRole("USER", "GLOBAL_ADMIN")
            )
            .addFilterBefore(roleFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(h -> h.frameOptions(fo -> fo.sameOrigin()));

        return http.build();
    }

    @Component
    @Order(2)
    public static class RoleHeaderAuthFilter extends OncePerRequestFilter {

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

            String role = request.getHeader(TenantFilter.ROLE_HEADER);
            if (role != null && !role.isBlank()) {
                var authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase().trim());
                var auth = new UsernamePasswordAuthenticationToken(
                        "user", null, Collections.singletonList(authority));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);
        }
    }
}
