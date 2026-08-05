package tancredidangelo.capstone.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.PostService;

import java.util.Objects;
import java.util.UUID;

@Component("authConfig")
public class AuthConfig {

    /// dependency injection

    private final PostService postService;

    public AuthConfig(PostService postService) {
        this.postService = postService;
    }


    // ---------------------  methods  -----------------------------------------------


    /// ACCOUNT OWNER CHECK (Long ID)
    public boolean isOwner(Long id, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Account principal)) {
            return false;
        }
        return principal.getId() != null && principal.getId().equals(id);
    }



    /// USER OWNER CHECK (UUID ID)
    public boolean isUserOwner(UUID userId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Account principal)) {
            return false;
        }
        return principal.getUser() != null && principal.getUser().getId().equals(userId);
    }



    /// ADMIN ROLE CHECK
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
    }



    /// ACCOUNT OWNER OR ADMIN
    public boolean isOwnerOrAdmin(Long id, Authentication authentication) {
        return isAdmin(authentication) || isOwner(id, authentication);
    }



    /// USER OWNER OR ADMIN
    public boolean isUserOwnerOrAdmin(UUID userId, Authentication authentication) {
        return isAdmin(authentication) || isUserOwner(userId, authentication);
    }



    /// POST OWNER CHECK
    public boolean isPostOwner(Long postId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Account principal)) {
            return false;
        }
        Post post = this.postService.findById(postId);
        return post != null && post.getAuthor().getId().equals(principal.getId());
    }



    /// POST OWNER OR ADMIN
    public boolean isPostOwnerOrAdmin(Long postId, Authentication authentication) {
        return isPostOwner(postId, authentication) || isAdmin(authentication);
    }
}