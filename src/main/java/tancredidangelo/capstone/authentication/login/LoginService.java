package tancredidangelo.capstone.authentication.login;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.authentication.login.LoginDTO.LoginRequestDTO;
import tancredidangelo.capstone.authentication.login.LoginDTO.LoginResponseDTO;
import tancredidangelo.capstone.entities.person.account.Account;
import tancredidangelo.capstone.entities.person.account.AccountService;
import tancredidangelo.capstone.exceptions.UnauthorizedException;
import tancredidangelo.capstone.security.JWTTools;

@Service
public class LoginService {

    /// dependency injection
    private final JWTTools jwtTools;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    public LoginService(JWTTools jwtTools, AccountService accountService, PasswordEncoder passwordEncoder) {
        this.jwtTools = jwtTools;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }


    /// -------------- methods -----------------------------------------------------------------------------------

    public LoginResponseDTO checkCredentialsAndVerifyToken(LoginRequestDTO payload) {

        if (this.accountService.existsByUsername(payload.username())) {
            Account found = this.accountService.findByUsername(payload.username());

            if (this.passwordEncoder.matches(payload.password(), found.getPassword())) {
                String token = this.jwtTools.generateToken(found);
                return new LoginResponseDTO(token);
            }
        }

        throw new UnauthorizedException("Wrong credentials. Try again.");
    }


}
