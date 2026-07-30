package tancredidangelo.capstone.entities.person.account;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.person.account.accountDTOs.*;


import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    /// dependency injection
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }



    // -------------------------  ENDPOINTS  ---------------------------------------------------------------


    /// USER AND ADMIN
    /// CREATE NEW ACCOUNT -> POST "[...](http://localhost:PORT/accounts)" {+payload}
    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public NewAccountResponseDTO registerNewAccount(@RequestBody @Valid NewAccountRequestDTO payload) {
        return new NewAccountResponseDTO(this.accountService.save(payload));
    }

    /// ADMIN
    /// FIND ACCOUNT BY ID -> GET "[...](http://localhost:PORT/accounts/{id})"
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Account getAccountById(@PathVariable Long id) {
        return this.accountService.findById(id);
    }


    /// ADMIN
    /// SEARCH ACCOUNT WITH FILTERS -> GET "[...](http://localhost:PORT/accounts)" + {params}
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<Account> getAllAccountsAndFilter(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String usernameMatch,
            @RequestParam(required = false) Boolean isBanned,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateOfRegistration") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return this.accountService.searchBannedAccounts(country, usernameMatch, isBanned, pageable);

    }



    /// USER + ADMIN
    /// SEARCH ACTIVE ACCOUNTS WITH FILTERS -> GET "[...](http://localhost:PORT/accounts)" + {params}
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Page<Account> getActiveAccountsAndFilter(
            @RequestParam(required = false) String usernameMatch,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return this.accountService.searchActiveAccounts(country, usernameMatch, tags, pageable);
    }



    /// USER + ADMIN
    /// UPDATE ACCOUNT BY ID -> PUT "[...](http://localhost:PORT/accounts/{id})" + {payload}
    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public UpdateAccountResponseDTO updateAccountById(@PathVariable Long id, @RequestBody @Valid UpdateAccountRequestDTO payload) {
        return this.accountService.updateById(id, payload);
    }


    /// USER + ADMIN
    /// UPDATE PASSWORD BY ID -> PUT "[...](http://localhost:PORT/accounts/{id}/password)" + {payload}
    @PutMapping("{id}/password")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public UpdatePasswordResponseDTO updatePasswordById(@PathVariable Long id, @RequestBody @Valid UpdatePasswordRequestDTO payload) {
        return this.accountService.updatePasswordById(id, payload);
    }








}

