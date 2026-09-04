package tancredidangelo.heliosspaces.entities.feedActions.notification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tancredidangelo.heliosspaces.entities.feedActions.follow.Follow;
import tancredidangelo.heliosspaces.entities.feedActions.notification.NotificationDTO.NotificationRequestDTO;
import tancredidangelo.heliosspaces.entities.feedActions.notification.NotificationDTO.NotificationResponseDTO;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.person.account.stack.AccountService;
import tancredidangelo.heliosspaces.entities.post.Post;
import tancredidangelo.heliosspaces.exceptions.BadRequestException;
import tancredidangelo.heliosspaces.exceptions.NotFoundException;
import tancredidangelo.heliosspaces.exceptions.UnauthorizedException;

@Service
@Slf4j
public class NotificationService {

    @PersistenceContext
    private final EntityManager entityManager;
    private final NotificationRepository notificationRepository;
    private final AccountService accountService;

    public NotificationService(
            EntityManager entityManager,
            NotificationRepository notificationRepository,
            AccountService accountService
    ) {
        this.entityManager = entityManager;
        this.notificationRepository = notificationRepository;
        this.accountService = accountService;
    }

    // ------------------------ METHODS --------------------------------------------

    /// CREATE NOTIFICATION
    @Transactional
    public void createNotification(NotificationRequestDTO payload) {

        if (payload.senderId().equals(payload.recipientId())) {
            throw new BadRequestException("Cannot send a notification to yourself.");
        }

        Account sender = this.accountService.findById(payload.senderId());
        Account recipient = this.accountService.findById(payload.recipientId());

        // Lightweight proxies to avoid circular dependency queries
        Post postRef = payload.postId() != null ? entityManager.getReference(Post.class, payload.postId()) : null;
        Follow followRef = payload.followId() != null ? entityManager.getReference(Follow.class, payload.followId()) : null;

        Notification newNotification = switch (payload.notificationType()) {
            case FOLLOW, FOLLOW_ACCEPTED -> new Notification(
                    payload.notificationType(),
                    sender,
                    recipient,
                    null,
                    followRef
            );
            case LIKE_TO_POST, COMMENT_TO_POST -> new Notification(
                    payload.notificationType(),
                    sender,
                    recipient,
                    postRef,
                    null
            );
            default -> throw new BadRequestException("Invalid or unsupported notification type: " + payload.notificationType());
        };

        this.notificationRepository.save(newNotification);
    }

    /// GET OWN NOTIFICATIONS
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getOwnNotifications(Authentication authentication, Pageable pageable) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return this.notificationRepository.findByRecipientId(authenticatedAccount.getId(), pageable)
                .map(NotificationResponseDTO::fromEntity);
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