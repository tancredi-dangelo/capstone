package tancredidangelo.capstone.entities.adminActions.flag;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.adminActions.flag.flagDTO.CreateFlagRequestDTO;
import tancredidangelo.capstone.entities.adminActions.flag.flagDTO.FlagResponseDTO;
import tancredidangelo.capstone.entities.person.account.stack.Account;

import java.util.UUID;

@RestController
@RequestMapping("/admin/flags")
@PreAuthorize("@authConfig.isAdmin(authentication)")
public class FlagController {

    /// dependency injection

    private final FlagService flagService;

    public FlagController(FlagService flagService) {
        this.flagService = flagService;
    }


    // -----------------  ENDPOINTS  --------------------------------------------------------

    /// CREATE FLAG
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlagResponseDTO createFlag(@RequestBody @Valid CreateFlagRequestDTO payload, Authentication authentication) {
        Account admin = (Account) authentication.getPrincipal();
        return flagService.createFlag(admin, payload);
    }


    /// GET FLAG BY ID
    @GetMapping("/{id}")
    public FlagResponseDTO getFlagById(@PathVariable Long id) {
        return flagService.getFlagById(id);
    }


    /// GET FLAG BY USER
    @GetMapping("/by-user/{userId}")
    public Page<FlagResponseDTO> getFlagsByUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return flagService.getFlagsByUser(userId, pageable);
    }
}