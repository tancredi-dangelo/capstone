package tancredidangelo.capstone.entities.feedActions.notification.NotificationDTO;

import jakarta.validation.constraints.NotNull;
import tancredidangelo.capstone.entities.feedActions.notification.NotificationType;

public record NotificationRequestDTO(
        @NotNull NotificationType notificationType,
        @NotNull Long senderId,
        @NotNull Long recipientId,
        Long postId,
        Long followId
) {

}
