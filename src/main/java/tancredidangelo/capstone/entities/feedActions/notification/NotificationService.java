package tancredidangelo.capstone.entities.feedActions.notification;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.exceptions.NotFoundException;
import tancredidangelo.capstone.exceptions.UnauthorizedException;

@Service
@Slf4j
public class NotificationService {

    /// dependency injection

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }


    // methods


    /// CREATE NOTIFICATION
    public Notification createNotification(Notification notification) {

        if (notification.getSender().getId().equals(notification.getRecipient().getId())) {
            return null;
        }
        return this.notificationRepository.save(notification);
    }



    /// GET OWN NOTIFICATIONS
    public Page<Notification> getOwnNotifications(Authentication authentication, Pageable pageable) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return this.notificationRepository.findByRecipientId(authenticatedAccount.getId(), pageable);
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