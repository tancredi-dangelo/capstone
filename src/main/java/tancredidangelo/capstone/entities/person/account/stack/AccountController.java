package tancredidangelo.capstone.entities.person.account.stack;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.NewAccountRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.SetAccountBanRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.UpdateAccountRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.UpdatePasswordRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.responses.OwnAccountResponseDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.responses.PublicAccountResponseDTO;

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


    // *******  PUBLIC / AUTHENTICATED METHODS *******

    /// GET CURRENT LOGGED ACCOUNT PROFILE -> GET "/accounts/me"
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public OwnAccountResponseDTO getMyAccount(Authentication authentication) {
        Account currentAccount = (Account) authentication.getPrincipal();
        Account freshAccount = this.accountService.findById(currentAccount.getId());
        return OwnAccountResponseDTO.fromEntity(freshAccount);
    }

    /// PUBLIC VS OWNER
    /// FIND ACCOUNT BY ID -> GET "/accounts/{id}"
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public PublicAccountResponseDTO getAccountById(@PathVariable Long id) {
        return this.accountService.findPublicDTOById(id);
    }


    /// USER + ADMIN
    /// SEARCH ACTIVE ACCOUNTS WITH FILTERS -> GET "/accounts/browse"
    @GetMapping("/browse")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public Page<PublicAccountResponseDTO> getActiveAccountsAndFilter(
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
    /// POST "/accounts/create"
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public OwnAccountResponseDTO createNewAccount(@RequestBody @Valid NewAccountRequestDTO payload, Authentication authentication) {
        UUID user_id = ((Account) Objects.requireNonNull(authentication.getPrincipal())).getUser().getId();
        return this.accountService.save(user_id, payload);
    }


    /// OWNER
    /// UPDATE ACCOUNT BY ID -> PUT "/accounts/{id}"
    @PutMapping("/{id}")
    @PreAuthorize("@authConfig.isOwner(#id, authentication)")
    public OwnAccountResponseDTO updateAccountById(@PathVariable Long id, @RequestBody @Valid UpdateAccountRequestDTO payload) {
        return this.accountService.updateById(id, payload);
    }


    /// OWNER
    /// UPDATE PASSWORD BY ID -> PUT "/accounts/{id}/password"
    @PutMapping("/{id}/password")
    @PreAuthorize("@authConfig.isOwner(#id, authentication)")
    public OwnAccountResponseDTO updatePasswordById(@PathVariable Long id, @RequestBody @Valid UpdatePasswordRequestDTO payload) {
        return this.accountService.updatePasswordById(id, payload);
    }


    // ******* ADMIN METHODS *******


    /// ADMIN
    /// SEARCH ACCOUNT WITH FILTERS -> GET "/accounts/search"
    @GetMapping("/search")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public Page<OwnAccountResponseDTO> getAllAccountsAndFilter(
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

        Page<Account> rawAccounts = this.accountService.searchBannedAccounts(country, usernameMatch, isBanned, pageable);

        return rawAccounts.map(OwnAccountResponseDTO::fromEntity);
    }


    /// ADMIN
    /// SEARCH ACCOUNTS BELONGING TO USER : USER_ID -> GET "/accounts/by-user/{userId}"
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public List<PublicAccountResponseDTO> findAccountByUserId(@PathVariable UUID userId) {
        List<Account> rawAccounts = this.accountService.findByUserId(userId);
        return rawAccounts.stream().map(PublicAccountResponseDTO::fromEntity).toList();
    }


    /// ADMIN
    /// SET ACCOUNT BAN -> PUT "/accounts/{id}/ban"
    @PutMapping("/{id}/ban")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public OwnAccountResponseDTO setAccountBan(@PathVariable Long id, @RequestBody @Valid SetAccountBanRequestDTO payload) {
        return this.accountService.setBanStatusById(id, payload);
    }


    // ****** OWNER OR ADMIN ******

    /// DELETE ACCOUNT BY ID -> DELETE "/accounts/{id}"
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authConfig.isOwnerOrAdmin(#id, authentication)")
    public void deleteAccountById(@PathVariable Long id) {
        this.accountService.deleteById(id);
    }

}