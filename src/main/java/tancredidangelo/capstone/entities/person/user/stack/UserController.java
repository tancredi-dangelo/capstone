package tancredidangelo.capstone.entities.person.user.stack;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.user.userDTOs.requests.UpdateEmailRequestDTO;
import tancredidangelo.capstone.entities.person.user.userDTOs.requests.UpdateFlagRequestDTO;
import tancredidangelo.capstone.entities.person.user.userDTOs.requests.UpdateUserRequestDTO;
import tancredidangelo.capstone.entities.person.user.userDTOs.responses.UserResponseDTO;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    /// dependency injection

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ------------------------- OWNER METHODS -------------------------

    /// OWNER -> GET OWN USER DETAILS -> GET "/users/me"
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UserResponseDTO getOwnUserDetails(Authentication authentication) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        this.userService.findById(authenticatedAccount.getUser().getId());
        return UserResponseDTO.fromEntity(authenticatedAccount.getUser());
    }

    /// OWNER -> UPDATE USER DETAILS -> PUT "/users/me"
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UserResponseDTO updateUserById(@RequestBody @Valid UpdateUserRequestDTO payload, Authentication authentication) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return this.userService.updateById(authenticatedAccount.getUser().getId(), payload);
    }

    /// OWNER -> UPDATE USER EMAIL -> PUT "/users/me/email"
    @PutMapping("/me/email")
    @PreAuthorize("isAuthenticated()")
    public UserResponseDTO updateEmailById(@RequestBody @Valid UpdateEmailRequestDTO payload, Authentication authentication) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return this.userService.updateEmailById(authenticatedAccount.getUser().getId(), payload);
    }

    // ------------------------- ADMIN METHODS -------------------------

    /// ADMIN -> SEARCH USERS WITH FILTERS -> GET "/users/search"
    @GetMapping("/search")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public Page<UserResponseDTO> getAllUsersAndFilter(
            @RequestParam(required = false) Boolean isFlagged,
            @RequestParam(required = false) String emailMatch,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "birthdate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return this.userService.searchUsers(isFlagged, emailMatch, country, birthdate, pageable);
    }

    /// ADMIN -> GET USER BY ID -> GET "/users/{id}"
    @GetMapping("/{id}")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public UserResponseDTO getUserById(@PathVariable UUID id) {
        User found = this.userService.findById(id);
        return UserResponseDTO.fromEntity(found);
    }

    /// ADMIN -> UPDATE USER FLAG -> PUT "/users/{id}/flag"
    @PutMapping("/{id}/flag")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public UserResponseDTO setUserFlag(@PathVariable UUID id, @RequestBody @Valid UpdateFlagRequestDTO payload) {
        return this.userService.setFlagById(id, payload);
    }

    // ------------------------- OWNER OR ADMIN METHODS -------------------------

    /// OWNER OR ADMIN -> DELETE USER BY ID -> DELETE "/users/{id}"
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authConfig.isUserOwnerOrAdmin(#id, authentication)")
    public void deleteUserById(@PathVariable UUID id) {
        this.userService.deleteById(id);
    }
}