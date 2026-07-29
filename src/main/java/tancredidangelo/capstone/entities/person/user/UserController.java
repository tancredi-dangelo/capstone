package tancredidangelo.capstone.entities.person.user;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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


    /// CREATE NEW USER -> GET "[...](http://localhost:PORT/users)" -> 201 CREATED
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public NewUserResponseDTO registerNewUser(@RequestBody NewUserRequestDTO payload) {
        return this.userService.save(payload);
    }




    /// GET ALL USERS -> GET "[...](http://localhost:PORT/users/search)" -> 200 OK
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
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



    /// GET USER BY ID -> GET "[...](http://localhost:PORT/users/{id})" -> 200 OK
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public UserResponseDTO getUserById(@RequestParam UUID id) {
        User found = this.userService.findById(id);
        return new UserResponseDTO(found.getId(), found.getFirstName(), found.getLastName(), found.getEmail(),  found.getBirthdate(), found.getCountry(), found.isFlagged());
    }




    /// UPDATE USER BY ID -> GET "[...](http://localhost:PORT/users/{id})" + {payload} -> 200 OK
    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public UpdateUserResponseDTO updateUserById(@RequestParam UUID id, @RequestBody @Valid UpdateUserRequestDTO payload) {
        return this.userService.updateById(id, payload);
    }


    /// UPDATE USER EMAIL BY ID -> GET "[...](http://localhost:PORT/users/{id})" + {payload} -> 200 OK
    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public UpdateEmailResponseDTO updateUserById(@RequestParam UUID id, @RequestBody @Valid UpdateEmailRequestDTO payload) {
        return this.userService.updateEmailById(id, payload);
    }


    ///



}
