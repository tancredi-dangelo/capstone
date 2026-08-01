package tancredidangelo.capstone.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import tancredidangelo.capstone.entities.person.account.Account;

import java.util.Objects;

@Component("authConfig")
public class AuthConfig {

    public boolean isOwner(Long id, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Account principal)) {
            return false;
        }
        System.out.println("DEBUG Authorities: " + authentication.getAuthorities());
        return principal.getId() != null && principal.getId().equals(id);
    }


    public boolean isAdmin(Authentication authentication) {
        if (authentication == null) { return false; }
        System.out.println("DEBUG Authorities: " + authentication.getAuthorities());
        return authentication.getAuthorities().stream().anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
    }


    public boolean isOwnerOrAdmin(Long id, Authentication authentication) {
        System.out.println("DEBUG Authorities: " + authentication.getAuthorities());
        return isAdmin(authentication) || isOwner(id, authentication);
    }
}
