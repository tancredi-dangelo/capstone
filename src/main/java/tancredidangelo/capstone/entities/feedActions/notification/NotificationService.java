package tancredidangelo.capstone.entities.feedActions.notification;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.feedActions.follow.Follow;
import tancredidangelo.capstone.entities.feedActions.follow.FollowService;
import tancredidangelo.capstone.entities.feedActions.notification.NotificationDTO.NotificationRequestDTO;
import tancredidangelo.capstone.entities.feedActions.notification.NotificationDTO.NotificationResponseDTO;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.account.stack.AccountService;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.PostService;
import tancredidangelo.capstone.exceptions.BadRequestException;
import tancredidangelo.capstone.exceptions.NotFoundException;
import tancredidangelo.capstone.exceptions.UnauthorizedException;

import java.util.List;

@Service
@Slf4j
public class NotificationService {

    /// dependency injection

    private final NotificationRepository notificationRepository;
    private final FollowService followService;
    private final AccountService accountService;
    private final PostService postService;

    public NotificationService(NotificationRepository notificationRepository, FollowService followService, AccountService accountService, PostService postService) {
        this.notificationRepository = notificationRepository;
        this.followService = followService;
        this.accountService = accountService;
        this.postService = postService;
    }


    // ------------------------  METHODS  --------------------------------------------


    /// CREATE NOTIFICATION
    @Transactional
    public Notification createNotification(NotificationRequestDTO payload) {

        if (payload.senderId().equals(payload.recipientId())) {
            throw new BadRequestException("Cannot send a notification to yourself.");
        }

        // case Follow
        if (payload.notificationType().equals(NotificationType.FOLLOW.name())) {
            Account sender = this.accountService.findById(payload.senderId());
            Account recipient = this.accountService.findById(payload.recipientId());
            Follow follow = this.followService.findById(payload.followId());
            Notification newNotification = new Notification(
                    NotificationType.FOLLOW,
                    sender,
                    recipient,
                    follow
            );

            return this.notificationRepository.save(newNotification);
        }


        // case interaction with Post
        if (payload.notificationType().equals(NotificationType.LIKE_TO_POST.name()) || payload.notificationType().equals(NotificationType.COMMENT_TO_POST.name())) {
            Account sender = this.accountService.findById(payload.senderId());
            Account recipient = this.accountService.findById(payload.recipientId());
            Post post = this.postService.findById(payload.postId());
            Notification newNotification = new Notification(
                    payload.notificationType().equals(NotificationType.LIKE_TO_POST.name()) ? NotificationType.LIKE_TO_POST : NotificationType.COMMENT_TO_POST,
                    sender,
                    recipient,
                    post
            );

            return this.notificationRepository.save(newNotification);
        }

        throw new BadRequestException("Invalid notification type.");
    }



    /// GET OWN NOTIFICATIONS
    public Page<NotificationResponseDTO> getOwnNotifications(Authentication authentication, Pageable pageable) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return (this.notificationRepository.findByRecipientId(authenticatedAccount.getId(), pageable).map(NotificationResponseDTO::fromEntity));
    }




    /// MARK NOTIFICATION AS READ
    @Transactional
    public Notification markAsRead(Long notificationId, Authentication authentication) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        Notification notification = this.notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found."));

        if (!notification.getRecipient().getId().equals(authenticatedAccount.getId())) {
            throw new UnauthorizedException("You are not allowed to modify this notification.");
        }

        notification.setRead(true);
        return this.notificationRepository.save(notification);
    }



    /// MARK ALL AS READ
    @Transactional
    public void markAllAsRead(Authentication authentication) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        this.notificationRepository.markAllAsReadByRecipientId(authenticatedAccount.getId());
    }
}