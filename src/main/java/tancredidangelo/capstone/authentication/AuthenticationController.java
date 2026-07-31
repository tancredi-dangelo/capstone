package tancredidangelo.capstone.authentication;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.authentication.AuthenticationDTO.AuthenticationRequestDTO;
import tancredidangelo.capstone.authentication.AuthenticationDTO.AuthenticationResponseDTO;
import tancredidangelo.capstone.firstRegistration.FirstRegistrationService;
import tancredidangelo.capstone.firstRegistration.firstRegistrationDTO.FirstRegistrationRequestDTO;
import tancredidangelo.capstone.firstRegistration.firstRegistrationDTO.FirstRegistrationResponseDTO;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    /// dependency injection
    private final AuthenticationService authenticationService;
    private final FirstRegistrationService registrationService;


    public AuthenticationController(AuthenticationService authenticationService, FirstRegistrationService registrationService) {
        this.authenticationService = authenticationService;
        this.registrationService = registrationService;
    }



    // ----------------- ENDPOINTS ---------------------------------------------------------------------------------

    /// REGISTRATION USER + FIRST ACCOUNT
    /// POST http:/localhost:PORT/auth/registration + {payload}
    @PostMapping("/registration/user")
    @ResponseStatus(HttpStatus.CREATED)
    public FirstRegistrationResponseDTO registerNewUserAndAccount(@RequestBody @Valid FirstRegistrationRequestDTO payload) {
        return this.registrationService.registerNewUserAndAccount(payload);
    }



    /// LOGIN ACCOUNT
    /// POST http:/localhost:PORT/auth/login + {payload}
    @PostMapping("/login")
    public AuthenticationResponseDTO loginAccount(@RequestBody @Valid AuthenticationRequestDTO payload) {
        return this.authenticationService.checkCredentialsAndVerifyToken(payload);
    }

}
