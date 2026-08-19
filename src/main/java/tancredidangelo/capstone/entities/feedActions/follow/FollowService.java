package tancredidangelo.capstone.entities.feedActions.follow;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional; // Usa Spring Transactional
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

        Account principal = (Account) authentication.getPrincipal();

        Account follower = this.accountService.findById(principal.getId());

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

        Account principal = (Account) authentication.getPrincipal();

        Account follower = this.accountService.findById(principal.getId());

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
    @Transactional(readOnly = true)
    public Follow findById(Long id) {
        return this.followRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Follow with ID " + id + " not found."));
    }


    /// EXISTS BY FOLLOWER ID AND FOLLOWED ID
    public boolean existsByFollowerAndFollowed(Long followerId, Long followedId) {
        return this.followRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    }


    /// IS ACCOUNT FOLLOWED BY USERNAME
    public boolean isAccountFollowed(Long authenticatedAccountId, String targetUsername) {
        Account targetAccount = this.accountService.findByUsername(targetUsername);
        return existsByFollowerAndFollowed(authenticatedAccountId, targetAccount.getId());
    }



    /// GET FOLLOWERS
    @Transactional(readOnly = true)
    public Page<FollowResponseDTO> getFollowers(Long accountId, Pageable pageable) {
        return this.followRepository
                .findByFollowedIdAndFollowStatus(accountId, FollowStatus.ACCEPTED, pageable)
                .map(FollowResponseDTO::fromEntity);
    }



    /// GET FOLLOWING
    @Transactional(readOnly = true)
    public Page<FollowResponseDTO> getFollowing(Long accountId, Pageable pageable) {
        return this.followRepository
                .findByFollowerIdAndFollowStatus(accountId, FollowStatus.ACCEPTED, pageable)
                .map(FollowResponseDTO::fromEntity);
    }
}