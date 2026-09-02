package tancredidangelo.capstone.entities.feedActions.follow;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.request.FollowRequestDTO;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.request.FollowResolveRequestDTO;

import tancredidangelo.capstone.entities.feedActions.follow.followDTO.response.FollowResponseDTO;

@RestController
@RequestMapping("/follows")
public class FollowController {

    /// dependency injection

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }


    // ------------------- ENDPOINTS --------------------------------------------------

    /// GET FOLLOW BY ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public FollowResponseDTO getFollowById(
            @PathVariable Long id
    ) {
        return FollowResponseDTO.fromEntity(this.followService.findById(id));
    }

    /// FOLLOW / REQUEST FOLLOW
    @PostMapping("/follow-request")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public FollowResponseDTO follow(
            @RequestBody @Valid FollowRequestDTO payload,
            Authentication authentication
    ) {
        return this.followService.follow(payload, authentication);
    }

    /// UNFOLLOW / CANCEL REQUEST
    @DeleteMapping("/unfollow-request/{targetAccountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void unfollow(@PathVariable Long targetAccountId, Authentication authentication) {
        this.followService.unfollow(targetAccountId, authentication);
    }

    /// GET FOLLOW STATUS
    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    public String getFollowStatus(
            @RequestParam Long followerId,
            @RequestParam Long targetAccountId
    ) {
        return this.followService.getFollowStatus(followerId, targetAccountId);
    }

    /// RESPOND FOLLOW REQUEST
    @PutMapping("/follow-requests/respond")
    @PreAuthorize("isAuthenticated()")
    public FollowResponseDTO respondToFollowRequest(
            @RequestBody @Valid FollowResolveRequestDTO payload,
            Authentication authentication
    ) {
        return this.followService.respondToFollowRequest(payload, authentication);
    }

    /// GET FOLLOWERS
    @GetMapping("/followers/{accountId}")
    public Page<FollowResponseDTO> getFollowers(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return this.followService.getFollowers(accountId, PageRequest.of(page, size));
    }

    /// GET FOLLOWING
    @GetMapping("/following/{accountId}")
    public Page<FollowResponseDTO> getFollowing(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return this.followService.getFollowing(accountId, PageRequest.of(page, size));
    }
}