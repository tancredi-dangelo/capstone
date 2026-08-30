package tancredidangelo.capstone.entities.feedActions.notification;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.feedActions.notification.NotificationDTO.NotificationRequestDTO;
import tancredidangelo.capstone.entities.feedActions.notification.NotificationDTO.NotificationResponseDTO;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    /// dependency injection

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    // --------------  ENDPOINTS  ----------------------------------------------------------


    /// CREATE NOTIFICATION
    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public NotificationResponseDTO createNotification(@RequestBody @Valid NotificationRequestDTO payload) {
        Notification newNotification = this.notificationService.createNotification(payload);
        return NotificationResponseDTO.fromEntity(newNotification);
    }


    /// GET OWN NOTIFICATIONS
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Page<NotificationResponseDTO> getOwnNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return this.notificationService.getOwnNotifications(authentication, pageable);
    }


    /// MARK AS READ
    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public Notification markAsRead(@PathVariable Long id, Authentication authentication) {
        return this.notificationService.markAsRead(id, authentication);
    }


    /// MARK ALL AS READ
    @PatchMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void markAllAsRead(Authentication authentication) {
        this.notificationService.markAllAsRead(authentication);
    }
}