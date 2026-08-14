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
import org.springframework.web.multipart.MultipartFile;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.NewAccountRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.UpdateAccountRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.UpdatePasswordRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.responses.AdminAccountResponseDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.responses.OwnAccountResponseDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.responses.PublicAccountResponseDTO;
import tancredidangelo.capstone.entities.tag.Tag;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    /// dependency injection

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // ------------------------- PUBLIC / AUTHENTICATED METHODS -------------------------

    /// SEARCH ACTIVE ACCOUNTS WITH FILTERS -> GET "/accounts/browse"
    @GetMapping("/explore")
    @PreAuthorize("isAuthenticated()")
    public Page<PublicAccountResponseDTO> getActiveAccountsAndFilter(
            @RequestParam(required = false) String usernameMatch,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return this.accountService.searchActiveAccounts(country, usernameMatch, tagIds, pageable);
    }

    /// GET ACCOUNT BY USERNAME
    @GetMapping("/{username}")
    @PreAuthorize(("isAuthenticated()"))
    public PublicAccountResponseDTO getByUsername(@PathVariable String username) {
        Account found = this.accountService.findByUsername(username);
        return PublicAccountResponseDTO.fromEntity(found);
    }



    // ------------------------- OWNER METHODS -------------------------


    /// OWNER -> GET CURRENT LOGGED ACCOUNT PROFILE -> GET "/accounts/me"
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public OwnAccountResponseDTO getOwnAccount(Authentication authentication) {
        Account currentAccount = (Account) authentication.getPrincipal();
        Account freshAccount = this.accountService.findById(currentAccount.getId());
        return OwnAccountResponseDTO.fromEntity(freshAccount);
    }


    /// OWNER -> AUTHENTICATED ACCOUNT REGISTERS NEW ACCOUNT -> POST "/accounts/create"
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public OwnAccountResponseDTO createNewAccount(@RequestBody @Valid NewAccountRequestDTO payload, Authentication authentication) {
        Account currentAccount = (Account) authentication.getPrincipal();
        UUID userId = currentAccount.getUser().getId();
        return this.accountService.save(userId, payload);
    }


    /// OWNER -> UPDATE ACCOUNT -> PUT "/accounts/me"
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public OwnAccountResponseDTO updateAccountById(@RequestBody @Valid UpdateAccountRequestDTO payload, Authentication authentication) {
        Account ownerAccount = (Account) authentication.getPrincipal();
        return this.accountService.updateById(ownerAccount.getId(), payload);
    }


    /// OWNER -> UPDATE PASSWORD -> PUT "/accounts/me/password"
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public OwnAccountResponseDTO updatePasswordById(@RequestBody @Valid UpdatePasswordRequestDTO payload, Authentication authentication) {
        Account ownerAccount = (Account) authentication.getPrincipal();
        return this.accountService.updatePasswordById(ownerAccount.getId(), payload);
    }


    /// OWNER -> UPDATE PROFILE PIC -> PATCH "/accounts/me/avatar"
    @PatchMapping("/me/avatar")
    @PreAuthorize("isAuthenticated()")
    public String uploadProfilePic(@RequestParam("file") MultipartFile file, Authentication authentication) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return this.accountService.updateAvatar(authenticatedAccount.getId(), file);
    }

    // ------------------------- ADMIN METHODS -------------------------

    /// ADMIN -> FIND ACCOUNT BY ID -> GET "/accounts/{id}"
    @GetMapping("/id/{id}")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public AdminAccountResponseDTO getAccountById(@PathVariable Long id) {
        return this.accountService.findAccountById(id);
    }

    /// ADMIN -> SEARCH ACCOUNTS WITH FILTERS -> GET "/accounts/search"
    @GetMapping("/search")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public Page<AdminAccountResponseDTO> getAllAccountsAndFilter(
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

    /// ADMIN -> SEARCH ACCOUNTS BELONGING TO USER -> GET "/accounts/by-user/{userId}"
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public List<AdminAccountResponseDTO> findAccountByUserId(@PathVariable UUID userId) {
        return this.accountService.findByUserId(userId);
    }

    // ------------------------- OWNER OR ADMIN METHODS -------------------------

    /// DELETE ACCOUNT BY ID -> DELETE "/accounts/{id}"
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authConfig.isOwnerOrAdmin(#id, authentication)")
    public void deleteAccountById(@PathVariable Long id) {
        this.accountService.deleteById(id);
    }
}