package tancredidangelo.capstone.entities.adminActions.ban;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.adminActions.ban.banDTO.response.BanResponseDTO;
import tancredidangelo.capstone.entities.adminActions.ban.banDTO.request.TemporaryBanRequestDTO;
import tancredidangelo.capstone.entities.adminActions.ban.banDTO.request.PermanentBanRequestDTO;

@RestController
@RequestMapping("/admin/bans")
@PreAuthorize("@authConfig.isAdmin(authentication)")
public class BanController {

    /// dependency injection

    private final BanService banService;

    public BanController(BanService banService) {
        this.banService = banService;
    }


    // ---------------  ENDPOINTS  -----------------------------------------------------------------

    /// CREATE TEMPORARY BAN
    @PostMapping("/create/temporary")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public BanResponseDTO createTemporaryBan(@RequestBody @Valid TemporaryBanRequestDTO payload, Authentication authentication) {
        return banService.createTemporaryBan(payload, authentication);
    }


    /// CREATE PERMANENT BAN
    @PostMapping("/create/permanent")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public BanResponseDTO createPermanentBan(@RequestBody @Valid PermanentBanRequestDTO payload, Authentication authentication) {
        return banService.createPermanentBan(payload, authentication);
    }


    /// REVOKE BAN
    @PutMapping("/{id}/revoke")
    public BanResponseDTO revokeBan(@PathVariable Long id) {
        return banService.revokeBan(id);
    }


    /// GET BAN BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BanResponseDTO getBanById(@PathVariable Long id) {
        return banService.getBanById(id);
    }



    /// GET BAN BY ACCOUNT ID
    @GetMapping("/by-account/{accountId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<BanResponseDTO> getBansByAccount(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return banService.getBansByAccount(accountId, pageable);
    }
}