package tancredidangelo.heliosspaces.authentication.registration.registrationAdmin;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.person.account.stack.AccountRepository;
import tancredidangelo.heliosspaces.entities.person.user.stack.User;
import tancredidangelo.heliosspaces.entities.person.user.stack.UserRepository;
import tancredidangelo.heliosspaces.exceptions.AlreadyExistsException;
import tancredidangelo.heliosspaces.exceptions.UnauthorizedException;
import tancredidangelo.heliosspaces.authentication.registration.registrationAdmin.adminRegistrationDTO.AdminRegistrationRequestDTO;
import tancredidangelo.heliosspaces.authentication.registration.registrationAdmin.adminRegistrationDTO.AdminRegistrationResponseDTO;
import tancredidangelo.heliosspaces.helpers.CountryCodeConverter;
import tancredidangelo.heliosspaces.security.JWTTools;


@Service
@Slf4j
public class AdminRegistrationService {

    /// dependency injection
    private final JWTTools jwtTools;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminRegistrationService(JWTTools jwtTools, AccountRepository accountRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, @Value("${admin.username}") String adminUsername, @Value("${admin.password}") String adminPassword) {
        this.jwtTools = jwtTools;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }


    /// -------------- methods -----------------------------------------------------------------------------------

    @Transactional
    public AdminRegistrationResponseDTO registerNewAdmin(AdminRegistrationRequestDTO payload) {

        if (this.userRepository.existsByEmail(payload.email())) {
            throw new AlreadyExistsException("An account with this email already exists.");
        }

        if (this.accountRepository.existsByUsername(payload.username())) {
            throw new AlreadyExistsException("This username is already being used. Please choose another username.");
        }

        User newAdminUser = new User(payload.firstName(), payload.lastName(), payload.email(), payload.birthdate(), CountryCodeConverter.toIsoCode(payload.country()));

        User savedAdminUser = this.userRepository.save(newAdminUser);

        if (payload.username().equals(this.adminUsername) && payload.password().equals(adminPassword)) {

            Account newAdminAccount = new Account(savedAdminUser, payload.username(), passwordEncoder.encode(payload.password()));

            Account savedAdmin = this.accountRepository.save(newAdminAccount);

            String adminToken = this.jwtTools.generateToken(savedAdmin);

            return new AdminRegistrationResponseDTO(adminToken);
        }

        throw new UnauthorizedException("Wrong credentials. Try again.");
    }

}
