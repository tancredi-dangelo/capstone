package tancredidangelo.capstone.authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.authentication.AuthenticationDTO.AuthenticationRequestDTO;
import tancredidangelo.capstone.authentication.AuthenticationDTO.AuthenticationResponseDTO;
import tancredidangelo.capstone.entities.person.account.Account;
import tancredidangelo.capstone.entities.person.account.AccountService;
import tancredidangelo.capstone.exceptions.UnauthorizedException;
import tancredidangelo.capstone.security.JWTTools;

@Service
public class AuthenticationService {

    /// dependency injection
    private final JWTTools jwtTools;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(JWTTools jwtTools, AccountService accountService, PasswordEncoder passwordEncoder) {
        this.jwtTools = jwtTools;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }


    /// -------------- methods -----------------------------------------------------------------------------------

    public AuthenticationResponseDTO checkCredentialsAndVerifyToken(AuthenticationRequestDTO payload) {

        if(this.accountService.existsByUsername(payload.username())) {
            Account found = this.accountService.findByUsername(payload.username());

            if (this.passwordEncoder.matches(payload.password(), found.getPassword())) {
                String token = this.jwtTools.generateToken(found);
                return new AuthenticationResponseDTO(token);
            }
        }

        throw new UnauthorizedException("Wrong credentials. Try again.");
    }


}
