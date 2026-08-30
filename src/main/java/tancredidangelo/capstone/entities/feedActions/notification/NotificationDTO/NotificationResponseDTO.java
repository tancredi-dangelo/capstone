package tancredidangelo.capstone.entities.feedActions.notification.NotificationDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tancredidangelo.capstone.entities.feedActions.notification.Notification;
import tancredidangelo.capstone.entities.feedActions.notification.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponseDTO (
        @NotNull Long id,
        @NotNull NotificationType notificationType,
        @NotBlank Long senderId,
        @NotBlank String senderUsername,
        @NotBlank Long recipientId,
        Long postId,
        Long followId,
        LocalDateTime timestamp,
        boolean isRead
) {
    public static NotificationResponseDTO fromEntity(Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getNotificationType(),
                notification.getSender().getId(),
                notification.getSender().getUsername(),
                notification.getRecipient().getId(),
                notification.getPost() != null ? notification.getPost().getId() : null,
                notification.getFollow() != null ? notification.getFollow().getId() : null,
                notification.getTimestamp(),
                notification.isRead()
        );
    }
}
