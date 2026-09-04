package tancredidangelo.heliosspaces.authentication.registration.registrationUser;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tancredidangelo.heliosspaces.cloudinary.CloudinaryService;
import tancredidangelo.heliosspaces.emailSender.EmailSender;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.person.account.stack.AccountRepository;
import tancredidangelo.heliosspaces.entities.person.user.stack.User;
import tancredidangelo.heliosspaces.entities.person.user.stack.UserRepository;
import tancredidangelo.heliosspaces.authentication.registration.registrationUser.UserRegistrationDTO.UserRegistrationRequestDTO;
import tancredidangelo.heliosspaces.authentication.registration.registrationUser.UserRegistrationDTO.UserRegistrationResponseDTO;
import tancredidangelo.heliosspaces.exceptions.AlreadyExistsException;
import tancredidangelo.heliosspaces.helpers.CountryCodeConverter;
import tancredidangelo.heliosspaces.security.JWTTools;

/// REGISTRATION USER + FIRST ACCOUNT

@Service
@Slf4j
public class UserRegistrationService {

    /// dependency injection
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final JWTTools jwtTools;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final CloudinaryService fileUploader;

    public UserRegistrationService(UserRepository userRepository, AccountRepository accountRepository, JWTTools jwtTools, PasswordEncoder passwordEncoder, EmailSender emailSender, CloudinaryService fileUploader) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.jwtTools = jwtTools;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.fileUploader = fileUploader;
    }



    // ------------------------ METHODS -------------------------------------------------------------------------

    @Transactional
    public UserRegistrationResponseDTO registerNewUserAndAccount(UserRegistrationRequestDTO payload) {

        if (this.userRepository.existsByEmail(payload.email())) {
            throw new AlreadyExistsException("An account with this email already exists.");
        }

        if (this.accountRepository.existsByUsername(payload.username())) {
            throw new AlreadyExistsException("This username is already being used. Please choose another username.");
        }

        // ** USER **

        User newUser = new User(
                payload.firstName(),
                payload.lastName(),
                payload.email(),
                payload.birthdate(),
                CountryCodeConverter.toIsoCode(payload.country())
                );


        User savedUser = this.userRepository.save(newUser);

        log.info("User saved. User_Id : {}", savedUser.getId());


        // ** ACCOUNT **

        Account newAccount = new Account(
                savedUser,
                payload.username(),
                this.passwordEncoder.encode(payload.password()),
                null,
                payload.bio(),
                payload.isPrivate(),
                payload.tags()
        );

        Account savedAccount = this.accountRepository.save(newAccount);
        log.info("Account registered with ID: {}.", savedAccount.getId());

        // send registration email
        this.emailSender.sendRegistrationEmail(savedUser);

        // ** GENERATE TOKEN **
        String token = this.jwtTools.generateToken(savedAccount);

        return new UserRegistrationResponseDTO(token);

    }



}
