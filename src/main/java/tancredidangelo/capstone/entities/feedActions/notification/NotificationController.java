package tancredidangelo.capstone.entities.feedActions.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    /// dependency injection

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    // --------------  ENDPOINTS  ----------------------------------------------------------


    /// GET OWN NOTIFICATIONS
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<Notification> getOwnNotifications(
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