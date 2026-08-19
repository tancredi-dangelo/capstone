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
import tancredidangelo.capstone.entities.person.account.stack.Account;

@RestController
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    // ---------------------- ENDPOINTS ------------------------------------------

    /// CHECK FOLLOW EXISTS BY FOLLOWER ID AND FOLLOWED ID
    @GetMapping("/check")
    @PreAuthorize("isAuthenticated()")
    public boolean followExists(@RequestParam Long followerId,
                                @RequestParam Long followedId) {
        return this.followService.existsByFollowerAndFollowed(followerId, followedId);
    }

    /// CHECK FOLLOW EXISTS BY TARGET USERNAME
    @GetMapping("/check/username")
    @PreAuthorize("isAuthenticated()")
    public boolean followExistsByTargetUsername(@RequestParam String targetUsername, Authentication authentication) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return this.followService.isAccountFollowed(authenticatedAccount.getId(), targetUsername);
    }

    /// FOLLOW PUBLIC ACCOUNT
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public FollowResponseDTO follow(
            @RequestBody @Valid FollowRequestDTO payload,
            Authentication authentication
    ) {
        return this.followService.followPublicAccount(payload, authentication);
    }

    /// REQUEST FOLLOW PRIVATE ACCOUNT
    @PostMapping("/request")
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