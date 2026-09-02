package tancredidangelo.capstone.entities.feedActions.follow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.request.FollowRequestDTO;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.request.FollowResolveRequestDTO;
import tancredidangelo.capstone.entities.feedActions.follow.followDTO.response.FollowResponseDTO;
import tancredidangelo.capstone.entities.feedActions.notification.NotificationDTO.NotificationRequestDTO;
import tancredidangelo.capstone.entities.feedActions.notification.NotificationService;
import tancredidangelo.capstone.entities.feedActions.notification.NotificationType;
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
    private final NotificationService notificationService;

    public FollowService(FollowRepository followRepository, AccountService accountService, NotificationService notificationService) {
        this.followRepository = followRepository;
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    // ------------- METHODS -----------------------------------------------------------------

    /// FIND BY ID
    @Transactional(readOnly = true)
    public Follow findById(Long id) {
        return this.followRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Follow not found."));
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

        follow.setFollowStatus(followed.isPrivate() ? FollowStatus.PENDING : FollowStatus.ACCEPTED);
        Follow saved = followRepository.save(follow);

        // Send follow or request notification
        this.notificationService.createNotification(new NotificationRequestDTO(
                NotificationType.FOLLOW,
                follower.getId(),
                followed.getId(),
                null,
                saved.getId()
        ));

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
    public String getFollowStatus(Long followerId, Long targetAccountId) {

        return followRepository.findByFollowerIdAndFollowedId(followerId, targetAccountId)
                .map(follow -> follow.getFollowStatus().name())
                .orElse("NONE");
    }

    /// RESPOND TO FOLLOW REQUEST
    @Transactional
    public FollowResponseDTO respondToFollowRequest(FollowResolveRequestDTO payload, Authentication authentication) {
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

        // Send follow accepted notification
        if (follow.getFollowStatus() == FollowStatus.ACCEPTED) {
            this.notificationService.createNotification(new NotificationRequestDTO(
                    NotificationType.FOLLOW_ACCEPTED,
                    authenticatedAccount.getId(),
                    follow.getFollower().getId(),
                    null,
                    follow.getId()
            ));
        }

        Follow updatedFollow = this.followRepository.save(follow);
        return FollowResponseDTO.fromEntity(updatedFollow);
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

    /// CHECK FOLLOW RELATIONSHIP EXISTS
    @Transactional(readOnly = true)
    public boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId) {
        return this.followRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    }
}