package tancredidangelo.capstone.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tancredidangelo.capstone.authentication.AuthenticationDTO.AuthenticationRequestDTO;
import tancredidangelo.capstone.authentication.AuthenticationDTO.AuthenticationResponseDTO;
import tancredidangelo.capstone.entities.person.user.UserService;
import tancredidangelo.capstone.entities.person.user.userDTOs.NewUserRequestDTO;
import tancredidangelo.capstone.entities.person.user.userDTOs.NewUserResponseDTO;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    /// dependency injection
    private final AuthenticationService authenticationService;
    private final UserService userService;


    public AuthenticationController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }



    // ----------------- ENDPOINTS ---------------------------------------------------------------------------------

    /// REGISTRATION USER
    /// POST http:/localhost:PORT/auth/registration + {payload}
    @PostMapping("/registration/user")
    @ResponseStatus(HttpStatus.CREATED)
    public NewUserResponseDTO registerNewUser(NewUserRequestDTO payload) {
        return this.userService.save(payload);
    }



    /// LOGIN ACCOUNT
    /// POST  http:/localhost:PORT/auth/login + {payload}
    @PostMapping("/login")
    public AuthenticationResponseDTO loginAccount(AuthenticationRequestDTO payload) {
        return this.authenticationService.checkCredentialsAndVerifyToken(payload);
    }

}
