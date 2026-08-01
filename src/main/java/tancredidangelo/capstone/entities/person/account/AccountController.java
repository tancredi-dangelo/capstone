package tancredidangelo.capstone.entities.person.account;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.person.account.accountDTOs.*;


import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    /// dependency injection
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }



    // -------------------------  ENDPOINTS  ---------------------------------------------------------------


    // *******  PUBLIC METHODS *******

    /// USER + ADMIN
    /// SEARCH ACTIVE ACCOUNTS WITH FILTERS -> GET "[...](http://localhost:PORT/accounts)" + {params}
    @GetMapping("/browse")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public Page<Account> getActiveAccountsAndFilter(
            @RequestParam(required = false) String usernameMatch,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return this.accountService.searchActiveAccounts(country, usernameMatch, tags, pageable);
    }





    // *******  OWNER METHODS *******


    /// OWNER -> AUTHENTICATED ACCOUNT REGISTERS NEW ACCOUNT
    /// POST "[...](http://localhost:PORT/accounts)" + {payload}
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public NewAccountResponseDTO createNewAccount(@RequestBody @Valid NewAccountRequestDTO payload, Authentication authentication) {
        UUID user_id = ((Account) Objects.requireNonNull(authentication.getPrincipal())).getUser().getId();
        return this.accountService.save(user_id, payload);
    }


    /// OWNER
    /// UPDATE ACCOUNT BY ID -> PUT "[...](http://localhost:PORT/accounts/{id})" + {payload}
    @PutMapping("/{id}")
    @PreAuthorize("@authConfig.isOwner(#id, authentication)")
    public UpdateAccountResponseDTO updateAccountById(@PathVariable Long id, @RequestBody @Valid UpdateAccountRequestDTO payload) {
        return this.accountService.updateById(id, payload);
    }


    /// OWNER
    /// UPDATE PASSWORD BY ID -> PUT "[...](http://localhost:PORT/accounts/{id}/password)" + {payload}
    @PutMapping("/{id}/password")
    @PreAuthorize("@authConfig.isOwner(#id, authentication)")
    public UpdatePasswordResponseDTO updatePasswordById(@PathVariable Long id, @RequestBody @Valid UpdatePasswordRequestDTO payload) {
        return this.accountService.updatePasswordById(id, payload);
    }



    // ******* ADMIN METHODS *******


    /// ADMIN
    /// SEARCH ACCOUNT WITH FILTERS -> GET "[...](http://localhost:PORT/accounts)" + {params}
    @GetMapping("/search")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
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



    /// ADMIN
    /// SET ACCOUNT BAN -> PUT "[...](http://localhost:PORT/accounts/{id})" + {payload}
    @PutMapping("/{id}/ban")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public SetAccountBanResponseDTO setAccountBan(@PathVariable Long id, @RequestBody @Valid SetAccountBanRequestDTO payload) {
        return this.accountService.setBanStatusById(id, payload);
    }




    // ****** OWNER OR ADMIN ******

    /// DELETE ACCOUNT BY ID -> PUT "[...](http://localhost:PORT/accounts/{id}/password)" + {payload}
    @DeleteMapping("/{id}")
    @PreAuthorize("@authConfig.isOwnerOrAdmin(#id, authentication)")
    public void deleteAccountById(@PathVariable Long id) {
        this.accountService.deleteById(id);
    }


    /// FIND OWN ACCOUNT / FIND ACCOUNT BY ID -> GET "[...](http://localhost:PORT/accounts/{id})"
    @GetMapping("/{id}")
    @PreAuthorize("@authConfig.isOwnerOrAdmin(#id, authentication)")
    public Account getAccountById(@PathVariable Long id) {
        return this.accountService.findById(id);
    }











}

