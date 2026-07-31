package tancredidangelo.capstone.entities.person.user;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.person.user.userDTOs.*;

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

    // ------------------------ ENDPOINTS ---------------------------------------------------------------------------------


    // *******  OWNER METHODS *******

    /// OWNER
    /// UPDATE USER BY ID -> PUT "[...](http://localhost:PORT/users/{id})" + {payload} -> 200 OK
    @PutMapping("{id}")
    @PreAuthorize("@authConfig.isOwner(#id, authentication)")
    public UpdateUserResponseDTO updateUserById(@PathVariable UUID id, @RequestBody @Valid UpdateUserRequestDTO payload) {
        return this.userService.updateById(id, payload);
    }

    /// OWNER
    /// UPDATE USER EMAIL BY ID -> PUT "[...](http://localhost:PORT/users/{id})" + {payload} -> 200 OK
    @PutMapping("{id}/email")
    @PreAuthorize("@authConfig.isOwner(#id, authentication)")
    public UpdateEmailResponseDTO updateEmailById(@PathVariable UUID id, @RequestBody @Valid UpdateEmailRequestDTO payload) {
        return this.userService.updateEmailById(id, payload);
    }



    // ****** ADMIN METHODS ******

    /// ADMIN
    /// GET ALL USERS -> GET "[...](http://localhost:PORT/users/search)" -> 200 OK
    @GetMapping("/search")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public Page<User> getAllUsersAndFilter(
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


    /// ADMIN
    /// GET USER BY ID -> GET "[...](http://localhost:PORT/users/{id})" -> 200 OK
    @GetMapping("{id}")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public UserResponseDTO getUserById(@PathVariable UUID id) {
        User found = this.userService.findById(id);
        return new UserResponseDTO(found.getId(), found.getFirstName(), found.getLastName(), found.getEmail(),  found.getBirthdate(), found.getCountry(), found.isFlagged());
    }



    /// ADMIN
    /// UPDATE USER FLAG BY ID -> PUT "[...](http://localhost:PORT/users/{id})" + {payload} -> 200 OK
    @PutMapping("{id}/flag")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public UpdateFlagResponseDTO setUserFlag(@PathVariable UUID id, @RequestBody @Valid UpdateFlagRequestDTO payload) {
        return this.userService.setFlagById(id, payload.flagValue());
    }





    // ******* OWNER OR ADMIN METHODS *******

    /// OWNER OR ADMIN
    /// DELETE USER BY ID -> DELETE "[...](http://localhost:PORT/users/{id})" -> 200 OK
    @DeleteMapping("/{id}")
    @PreAuthorize("@authConfig.isOwnerOrAdmin(#id, authentication)")
    public void deleteUserById(@PathVariable UUID id) {
        this.userService.deleteById(id);
    }

}
