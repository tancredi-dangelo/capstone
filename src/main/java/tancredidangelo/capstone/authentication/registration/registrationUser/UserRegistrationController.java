package tancredidangelo.capstone.authentication.registration.registrationUser;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import tancredidangelo.capstone.authentication.registration.registrationUser.UserRegistrationDTO.UserRegistrationRequestDTO;
import tancredidangelo.capstone.authentication.registration.registrationUser.UserRegistrationDTO.UserRegistrationResponseDTO;

@RestController
@RequestMapping("/auth")
public class UserRegistrationController {


    /// dependency injection
    private final UserRegistrationService registrationService;


    public UserRegistrationController(UserRegistrationService registrationService) {
        this.registrationService = registrationService;
    }


    // ----------------- ENDPOINTS ---------------------------------------------------------------------------------

    /// REGISTRATION USER + FIRST ACCOUNT
    /// POST http:/localhost:PORT/auth/registration + {payload}
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationResponseDTO registerNewUserAndAccount(@RequestBody @Valid UserRegistrationRequestDTO payload) {
        return this.registrationService.registerNewUserAndAccount(payload);
    }


}
