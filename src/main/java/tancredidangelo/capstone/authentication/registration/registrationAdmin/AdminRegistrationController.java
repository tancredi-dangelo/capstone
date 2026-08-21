package tancredidangelo.capstone.authentication.registration.registrationAdmin;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.authentication.registration.registrationAdmin.adminRegistrationDTO.AdminRegistrationRequestDTO;
import tancredidangelo.capstone.authentication.registration.registrationAdmin.adminRegistrationDTO.AdminRegistrationResponseDTO;

@RestController
@RequestMapping("/auth")
public class AdminRegistrationController {

    /// dependency injection
    private final AdminRegistrationService adminRegistrationService;


    public AdminRegistrationController(AdminRegistrationService adminRegistrationService) {
        this.adminRegistrationService = adminRegistrationService;
    }


    // ------------------ ENDPOINTS ------------------------------------------------------------

    /// ADMIN FIRST REGISTRATION
    @PostMapping("/admin/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminRegistrationResponseDTO registerAdmin(@RequestBody @Valid AdminRegistrationRequestDTO payload) {
        return this.adminRegistrationService.registerNewAdmin(payload);
    }

}
