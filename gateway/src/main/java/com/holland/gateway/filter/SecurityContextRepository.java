package com.holland.gateway.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        // Don't know yet where this is for.
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        // JwtAuthenticationToken and GuestAuthenticationToken are custom Authentication tokens.
//        Authentication authentication = (/* check if authenticated based on headers in serverWebExchange */) ?
//                new JwtAuthenticationToken(...) :
//        new GuestAuthenticationToken();
        Authentication authentication = new Authentication(){
            @Override
            public String getName() {
                return "name";
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                ArrayList<GrantedAuthority> objects = new ArrayList<>();
                objects.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                return objects;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean b) throws IllegalArgumentException {

            }
        };

        return Mono.just(new SecurityContextImpl(authentication));
    }
}