package tancredidangelo.capstone.entities.feedActions.follow;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.request.FollowRequestDTO;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.request.FollowResolveRequestDTO;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.response.FollowPendingResponseDTO;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.response.FollowResponseDTO;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.response.FollowResolvedResponseDTO;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.account.stack.AccountService;
import tancredidangelo.capstone.exceptions.AlreadyExistsException;
import tancredidangelo.capstone.exceptions.BadRequestException;
import tancredidangelo.capstone.exceptions.NotFoundException;
import tancredidangelo.capstone.exceptions.UnauthorizedException;

import java.time.LocalDateTime;

@Service
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;
    private final AccountService accountService;

    public FollowService(FollowRepository followRepository, AccountService accountService) {
        this.followRepository = followRepository;
        this.accountService = accountService;
    }

    // ------------------- METHODS ---------------------------------------------

    /// SAVE FOLLOW (PUBLIC ACCOUNT)
    @Transactional
    public FollowResponseDTO followPublicAccount(FollowRequestDTO payload, Authentication authentication) {

        Account follower = (Account) authentication.getPrincipal();

        if (follower.getId().equals(payload.followedId())) {
            throw new BadRequestException("An account cannot follow itself.");
        }

        if (this.followRepository.existsByFollowerIdAndFollowedId(follower.getId(), payload.followedId())) {
            throw new AlreadyExistsException("Follow relationship already exists or is pending.");
        }

        Account followed = this.accountService.findById(payload.followedId());

        Follow newFollow = new Follow(follower, followed);
        newFollow.setFollowStatus(FollowStatus.ACCEPTED);

        Follow saved = this.followRepository.save(newFollow);
        log.info("Account {} is now following Account {}.", follower.getId(), followed.getId());

        return FollowResponseDTO.fromEntity(saved);
    }




    /// SAVE FOLLOW (PRIVATE ACCOUNT -> REQUEST PENDING)
    @Transactional
    public FollowPendingResponseDTO requestFollow(FollowRequestDTO payload, Authentication authentication) {

        Account follower = (Account) authentication.getPrincipal();

        if (follower.getId().equals(payload.followedId())) {
            throw new BadRequestException("An account cannot follow itself.");
        }

        if (this.followRepository.existsByFollowerIdAndFollowedId(follower.getId(), payload.followedId())) {
            throw new AlreadyExistsException("Follow relationship already exists or is pending.");
        }

        Account followed = this.accountService.findById(payload.followedId());

        Follow follow = new Follow(follower, followed);

        log.info("Follow request sent from Account {} to Account {}.", follower.getId(), followed.getId());
        Follow saved = this.followRepository.save(follow);

        return FollowPendingResponseDTO.fromEntity(saved);
    }




    /// SAVE FOLLOW (PRIVATE ACCOUNT -> REQUEST ANSWERED)
    @Transactional
    public FollowResolvedResponseDTO respondToFollowRequest(Long followId, FollowResolveRequestDTO payload, Authentication authentication) {

        Account authenticatedAccount = (Account) authentication.getPrincipal();
        Follow follow = findById(followId);

        if (!follow.getFollowed().getId().equals(authenticatedAccount.getId())) {
            throw new UnauthorizedException("You are not authorized to respond to this follow request.");
        }

        if (follow.getFollowStatus() != FollowStatus.PENDING) {
            throw new BadRequestException("This follow request has already been processed.");
        }

        follow.setFollowStatus(payload.value() ? FollowStatus.ACCEPTED : FollowStatus.REFUSED);
        follow.setResponseDate(LocalDateTime.now());
        Follow saved = this.followRepository.save(follow);

        log.info("Follow request {} status updated to {}.", saved.getId(), follow.getFollowStatus());

        return FollowResolvedResponseDTO.fromEntity(saved);
    }




    /// UNFOLLOW (delete)
    @Transactional
    public void unfollow(Long targetAccountId, Authentication authentication) {

        Account follower = (Account) authentication.getPrincipal();

        Follow follow = this.followRepository.findByFollowerIdAndFollowedId(follower.getId(), targetAccountId)
                .orElseThrow(() -> new NotFoundException("Follow relationship not found."));

        this.followRepository.delete(follow);
        log.info("Account {} unfollowed Account {}.", follower.getId(), targetAccountId);
    }



    /// FIND BY ID
    public Follow findById(Long id) {
        return this.followRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Follow with ID " + id + " not found."));
    }



    /// GET FOLLOWERS
    public Page<FollowResponseDTO> getFollowers(Long accountId, Pageable pageable) {
        return this.followRepository
                .findByFollowedIdAndFollowStatus(accountId, FollowStatus.ACCEPTED, pageable)
                .map(FollowResponseDTO::fromEntity);
    }



    /// GET FOLLOWING
    public Page<FollowResponseDTO> getFollowing(Long accountId, Pageable pageable) {
        return this.followRepository
                .findByFollowerIdAndFollowStatus(accountId, FollowStatus.ACCEPTED, pageable)
                .map(FollowResponseDTO::fromEntity);
    }
}