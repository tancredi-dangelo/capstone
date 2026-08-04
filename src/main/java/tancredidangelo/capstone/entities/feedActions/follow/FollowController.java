package tancredidangelo.capstone.entities.feedActions.follow;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.request.FollowRequestDTO;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.request.FollowResolveRequestDTO;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.response.FollowPendingResponseDTO;

import tancredidangelo.capstone.entities.feedActions.follow.followDTO.response.FollowResponseDTO;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.response.FollowResolvedResponseDTO;

@RestController
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    // ---------------------- ENDPOINTS ------------------------------------------

    /// FOLLOW PUBLIC ACCOUNT
    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public FollowResponseDTO follow(
            @RequestBody @Valid FollowRequestDTO payload,
            Authentication authentication
    ) {
        return this.followService.followPublicAccount(payload, authentication);
    }

    /// REQUEST FOLLOW PRIVATE ACCOUNT
    @PostMapping("/request") // Corretto il typo "/request}"
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public FollowPendingResponseDTO requestFollow(
            @RequestBody @Valid FollowRequestDTO payload,
            Authentication authentication
    ) {
        return this.followService.requestFollow(payload, authentication);
    }

    /// ANSWER FOLLOW REQUEST PRIVATE ACCOUNT
    @PutMapping("/{followId}/respond")
    @PreAuthorize("isAuthenticated()")
    public FollowResolvedResponseDTO respondToFollowRequest(
            @PathVariable Long followId,
            @RequestBody @Valid FollowResolveRequestDTO payload,
            Authentication authentication
    ) {
        return this.followService.respondToFollowRequest(followId, payload, authentication);
    }

    /// UNFOLLOW
    @DeleteMapping("/unfollow/{targetAccountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void unfollow(@PathVariable Long targetAccountId, Authentication authentication) {
        this.followService.unfollow(targetAccountId, authentication);
    }

    /// GET ACCOUNT FOLLOWERS
    @GetMapping("/followers/{accountId}")
    public Page<FollowResponseDTO> getFollowers(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return this.followService.getFollowers(accountId, pageable);
    }

    /// GET ACCOUNT FOLLOWING
    @GetMapping("/following/{accountId}")
    public Page<FollowResponseDTO> getFollowing(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return this.followService.getFollowing(accountId, pageable);
    }
}