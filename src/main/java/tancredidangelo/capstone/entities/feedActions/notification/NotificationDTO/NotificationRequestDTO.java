package tancredidangelo.capstone.entities.feedActions.notification.NotificationDTO;

import jakarta.validation.constraints.NotNull;

public record NotificationRequestDTO(
        @NotNull String notificationType,
        @NotNull Long senderId,
        @NotNull Long recipientId,
        Long postId,
        Long followId
) {

}
