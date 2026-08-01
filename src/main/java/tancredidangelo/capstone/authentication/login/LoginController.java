package tancredidangelo.capstone.authentication.login;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.authentication.login.LoginDTO.LoginRequestDTO;
import tancredidangelo.capstone.authentication.login.LoginDTO.LoginResponseDTO;

@RestController
@RequestMapping("/auth")
public class LoginController {

    /// dependency injection
    private final LoginService authenticationService;


    public LoginController(LoginService authenticationService) {
        this.authenticationService = authenticationService;
    }


    // ----------------- ENDPOINTS ---------------------------------------------------------------------------------


    /// LOGIN ACCOUNT
    /// POST http:/localhost:PORT/auth/login + {payload}
    @PostMapping("/login")
    public LoginResponseDTO loginAccount(@RequestBody @Valid LoginRequestDTO payload) {
        return this.authenticationService.checkCredentialsAndVerifyToken(payload);
    }

}
