package tancredidangelo.capstone.entities.person.account.stack;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.NewAccountRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.SetAccountBanRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.UpdateAccountRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.UpdatePasswordRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.responses.OwnAccountResponseDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.responses.PublicAccountResponseDTO;
import tancredidangelo.capstone.entities.person.user.stack.User;
import tancredidangelo.capstone.entities.person.user.stack.UserService;
import tancredidangelo.capstone.exceptions.AlreadyExistsException;
import tancredidangelo.capstone.exceptions.NotFoundException;
import tancredidangelo.capstone.exceptions.ValidationException;
import tancredidangelo.capstone.specifications.AccountSpecification;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AccountService {

    /// dependency injection
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    // -------------------------- USER METHODS -------------------------------------------------------------------------


    /// CREATE NEW ACCOUNT
    @Transactional
    public OwnAccountResponseDTO save(UUID user_id, NewAccountRequestDTO payload) {

        if (this.accountRepository.existsByUsername(payload.username())) {
            throw new AlreadyExistsException("This username is already being used. Please choose another username.");
        }

        User userFound = this.userService.findById(user_id);

        Account newAccount = new Account(
                userFound,
                payload.username(),
                passwordEncoder.encode(payload.password()),
                payload.profilePicUrl(),
                payload.bio(),
                payload.tags()
        );

        Account saved = this.accountRepository.save(newAccount);

        return OwnAccountResponseDTO.fromEntity(saved);
    }


    /// FIND ACTIVE ACCOUNTS -> USER, ADMIN
    public Page<PublicAccountResponseDTO> searchActiveAccounts(String country, String usernameMatch, List<String> tags, Pageable pageable) {
        Specification<Account> spec = AccountSpecification.filterActiveAccounts(country, usernameMatch, tags);
        Page<Account> rawAccounts = this.accountRepository.findAll(spec, pageable);
        return rawAccounts.map(PublicAccountResponseDTO::fromEntity);
    }


    /// UPDATE ACCOUNT -> OWNER
    @Transactional
    public OwnAccountResponseDTO updateById(Long id, UpdateAccountRequestDTO payload) {
        Account found = findById(id);

        if (payload.username() != null && !found.getUsername().equalsIgnoreCase(payload.username())) {
            if (this.accountRepository.existsByUsername(payload.username())) {
                throw new AlreadyExistsException("This username is already taken.");
            }
            found.setUsername(payload.username());
        }

        if (payload.profilePicUrl() != null) found.setProfilePicUrl(payload.profilePicUrl());
        if (payload.bio() != null) found.setBio(payload.bio());
        if (payload.tags() != null) found.setTags(payload.tags());

        Account saved = this.accountRepository.save(found);
        return OwnAccountResponseDTO.fromEntity(saved);
    }


    /// UPDATE PASSWORD -> OWNER
    @Transactional
    public OwnAccountResponseDTO updatePasswordById(Long id, UpdatePasswordRequestDTO payload) {

        Account found = findById(id);

        if (!this.passwordEncoder.matches(payload.oldPassword(), found.getPassword())) {
            throw new ValidationException("Old password is not matching. Please try again.");
        }

        if (this.passwordEncoder.matches(payload.newPassword(), found.getPassword())) {
            throw new ValidationException("New password must be different from the old one!");
        }

        found.setPassword(passwordEncoder.encode(payload.newPassword()));

        Account updated = this.accountRepository.save(found);
        log.info("Password successfully updated for Account ID {}", id);

        return OwnAccountResponseDTO.fromEntity(updated);
    }


    /// DELETE ACCOUNT -> OWNER / ADMIN
    @Transactional
    public void deleteById(Long id) {
        Account found = findById(id);
        this.accountRepository.deleteById(found.getId());
    }



    // ------------------------------- ADMIN METHODS --------------------------------------------------------------


    /// FIND ACCOUNT ENTITY BY ID -> INTERNAL, ADMIN
    public Account findById(Long id) {
        return this.accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account not found."));
    }


    /// FIND PUBLIC DTO BY ID -> PUBLIC
    public PublicAccountResponseDTO findPublicDTOById(Long id) {
        return PublicAccountResponseDTO.fromEntity(findById(id));
    }


    /// FIND BY USERNAME -> ADMIN, IT
    public Account findByUsername(String username) {
        return (this.accountRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Account not found.")));
    }


    /// FIND ACCOUNTS BY USER ID
    public List<Account> findByUserId(UUID userId) {
        return this.accountRepository.findByUserId(userId);
    }


    /// EXISTS BY USERNAME -> IT, ADMIN
    public boolean existsByUsername(String username) {
        return this.accountRepository.existsByUsername(username);
    }


    /// FIND BANNED ACCOUNTS -> ADMIN
    public Page<Account> searchBannedAccounts(String country, String usernameMatch, Boolean isBanned, Pageable pageable) {
        Specification<Account> spec = AccountSpecification.filterAccounts(country, usernameMatch, isBanned);
        return this.accountRepository.findAll(spec, pageable);
    }


    /// BAN ACCOUNT
    @Transactional
    public OwnAccountResponseDTO setBanStatusById(Long id, SetAccountBanRequestDTO payload) {
        Account found = findById(id);
        found.setBanned(payload.value());
        Account banned = this.accountRepository.save(found);
        log.info("Ban status updated to {} for account ID {}", payload.value(), id);
        return OwnAccountResponseDTO.fromEntity(banned);
    }

}