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

    /// dependency injection

    private final FollowRepository followRepository;
    private final AccountService accountService;

    public FollowService(FollowRepository followRepository, AccountService accountService) {
        this.followRepository = followRepository;
        this.accountService = accountService;
    }


    // ------------- METHODS -----------------------------------------------------------------

    /// FIND BY ID
    public Follow findById(Long id) {
        return this.followRepository.findById(id).orElseThrow(() -> new NotFoundException("Follow not found."));
    }


    /// FOLLOW ACCOUNT
    @Transactional
    public FollowResponseDTO follow(FollowRequestDTO payload, Authentication authentication) {
        Account follower = (Account) authentication.getPrincipal();

        if (follower.getId().equals(payload.followedId())) {
            throw new BadRequestException("An account cannot follow itself.");
        }

        if (followRepository.existsByFollowerIdAndFollowedId(follower.getId(), payload.followedId())) {
            throw new AlreadyExistsException("Follow relationship already exists or is pending.");
        }

        Account followed = accountService.findById(payload.followedId());
        Follow follow = new Follow(follower, followed);


        if (followed.getIsPrivate()) {
            follow.setFollowStatus(FollowStatus.PENDING);
        } else {
            follow.setFollowStatus(FollowStatus.ACCEPTED);
        }

        Follow saved = followRepository.save(follow);
        return FollowResponseDTO.fromEntity(saved);
    }


    /// UNFOLLOW ACCOUNT
    @Transactional
    public void unfollow(Long targetAccountId, Authentication authentication) {
        Account follower = (Account) authentication.getPrincipal();

        Follow follow = followRepository.findByFollowerIdAndFollowedId(follower.getId(), targetAccountId)
                .orElseThrow(() -> new NotFoundException("Follow relationship not found."));

        followRepository.delete(follow);
    }



    /// GET FOLLOW STATUS
    @Transactional(readOnly = true)
    public String getFollowStatus(Long targetAccountId, Authentication authentication) {
        Account follower = (Account) authentication.getPrincipal();

        return followRepository.findByFollowerIdAndFollowedId(follower.getId(), targetAccountId)
                .map(follow -> follow.getFollowStatus().name())
                .orElse("NONE");
    }


    /// RESPOND TO FOLLOW REQUEST
    @Transactional
    public FollowResolvedResponseDTO respondToFollowRequest(FollowResolveRequestDTO payload, Authentication authentication) {

        Account authenticatedAccount = (Account) authentication.getPrincipal();

        Follow follow = followRepository.findById(payload.followId())
                .orElseThrow(() -> new NotFoundException("Follow request not found."));

        if (!follow.getFollowed().getId().equals(authenticatedAccount.getId())) {
            throw new UnauthorizedException("Not authorized to respond to this follow request.");
        }

        if (follow.getFollowStatus() != FollowStatus.PENDING) {
            throw new BadRequestException("Follow request already processed.");
        }

        follow.setFollowStatus(payload.value() ? FollowStatus.ACCEPTED : FollowStatus.REFUSED);
        follow.setResponseDate(LocalDateTime.now());

        return FollowResolvedResponseDTO.fromEntity(followRepository.save(follow));
    }


    /// GET ACCOUNT FOLLOWERS
    @Transactional(readOnly = true)
    public Page<FollowResponseDTO> getFollowers(Long accountId, Pageable pageable) {
        return followRepository.findByFollowedIdAndFollowStatus(accountId, FollowStatus.ACCEPTED, pageable)
                .map(FollowResponseDTO::fromEntity);
    }


    /// GET ACCOUNT FOLLOWING
    @Transactional(readOnly = true)
    public Page<FollowResponseDTO> getFollowing(Long accountId, Pageable pageable) {
        return followRepository.findByFollowerIdAndFollowStatus(accountId, FollowStatus.ACCEPTED, pageable)
                .map(FollowResponseDTO::fromEntity);
    }


    public boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId) {
        return this.followRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    }


    /// UPDATE FOLLOW BY ID
    public FollowResponseDTO updateFollowById(FollowResolveRequestDTO payload) {
        Follow found = findById(payload.followId());
        found.setFollowStatus(payload.value() ? FollowStatus.ACCEPTED: FollowStatus.REFUSED);
        Follow saved = this.followRepository.save(found);
        return FollowResponseDTO.fromEntity(saved);
    }


}