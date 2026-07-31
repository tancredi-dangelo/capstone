package tancredidangelo.capstone.firstRegistration;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.person.account.Account;
import tancredidangelo.capstone.entities.person.account.AccountRepository;
import tancredidangelo.capstone.entities.person.user.User;
import tancredidangelo.capstone.entities.person.user.UserRepository;
import tancredidangelo.capstone.firstRegistration.firstRegistrationDTO.FirstRegistrationRequestDTO;
import tancredidangelo.capstone.firstRegistration.firstRegistrationDTO.FirstRegistrationResponseDTO;
import tancredidangelo.capstone.exceptions.AlreadyExistsException;
import tancredidangelo.capstone.security.JWTTools;

/// REGISTRATION USER + FIRST ACCOUNT

@Service
@Slf4j
public class FirstRegistrationService {

    /// dependency injection
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final JWTTools jwtTools;
    private final PasswordEncoder passwordEncoder;

    public FirstRegistrationService(UserRepository userRepository, AccountRepository accountRepository, JWTTools jwtTools, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.jwtTools = jwtTools;
        this.passwordEncoder = passwordEncoder;
    }



    // ------------------------ METHODS -------------------------------------------------------------------------

    @Transactional
    public FirstRegistrationResponseDTO registerNewUserAndAccount(FirstRegistrationRequestDTO payload) {

        if (this.userRepository.existsByEmail(payload.email())) {
            throw new AlreadyExistsException("An account with this email already exists.");
        }

        if (this.accountRepository.existsByUsername(payload.username())) {
            throw new AlreadyExistsException("This username is already being used. Please choose another username.");
        }

        // ** USER **

        User newUser = new User();
        newUser.setFirstName(payload.firstName());
        newUser.setLastName(payload.lastName());
        newUser.setEmail(payload.email());
        newUser.setBirthdate(payload.birthdate());
        newUser.setCountry(payload.country());

        User savedUser = this.userRepository.save(newUser);
        log.info("User saved. User_Id : {}", savedUser.getId());


        // ** ACCOUNT **

        Account newAccount = new Account();
        newAccount.setUser(savedUser);
        newAccount.setUsername(payload.username());
        newAccount.setPassword(this.passwordEncoder.encode(payload.password()));
        newAccount.setProfilePicUrl(payload.profilePicUrl());
        newAccount.setTags(payload.tags());

        Account savedAccount = this.accountRepository.save(newAccount);
        log.info("Account registered with ID: {}.", savedAccount.getId());


        // ** GENERATE TOKEN **

        String token = this.jwtTools.generateToken(savedAccount);


        return new FirstRegistrationResponseDTO(token);

    }



}
