package tancredidangelo.capstone.entities.person.account.stack;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.person.account.accountDTOs.newAccount.NewAccountRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.newAccount.NewAccountResponseDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.updateAccount.*;
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
    private  final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    // -------------------------- USER METHODS -------------------------------------------------------------------------


    /// CREATE NEW ACCOUNT
    @Transactional
    public NewAccountResponseDTO save(UUID user_id, NewAccountRequestDTO payload) {

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

        return new NewAccountResponseDTO(saved.getId());
    }


    /// FIND BY USERNAME -> USER, ADMIN, IT
    public Account findByUsername(String username) {
        return this.accountRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Account not found."));
    }


    /// FIND ACTIVE ACCOUNTS -> USER, ADMIN
    public Page<Account> searchActiveAccounts(String country, String usernameMatch, List<String> tags, Pageable pageable) {
        Specification<Account> spec = AccountSpecification.filterActiveAccounts(country, usernameMatch, tags);
        return this.accountRepository.findAll(spec, pageable);
    }


    /// UPDATE ACCOUNT -> OWNER
    @Transactional
    public UpdateAccountResponseDTO updateById(Long id, UpdateAccountRequestDTO payload) {
        Account found = findById(id);

        found.setUsername(payload.username());
        found.setProfilePicUrl(payload.profilePicUrl());
        found.setTags(payload.tags());

        Account saved = this.accountRepository.save(found);
        return new UpdateAccountResponseDTO(saved.getId());
    }


    /// UPDATE PASSWORD -> OWNER
    @Transactional
    public UpdatePasswordResponseDTO updatePasswordById(Long id, UpdatePasswordRequestDTO payload) {

        Account found = findById(id);

        if (!this.passwordEncoder.matches(payload.oldPassword(), found.getPassword())) {
            throw new ValidationException("Old password is not matching. Please try again.");
        }

        if (this.passwordEncoder.matches(payload.newPassword(), found.getPassword())) {
            throw new ValidationException("New password must be different from the old one!");
        }

        found.setPassword(passwordEncoder.encode(payload.newPassword()));

        Account updated = this.accountRepository.save(found);
        log.info("Password successfully updated");

        return new UpdatePasswordResponseDTO(updated.getId());
    }


    /// DELETE ACCOUNT -> OWNER
    @Transactional
    public void deleteById(Long id) {
        Account found = findById(id);
        this.accountRepository.deleteById(id);
    }



    // ------------------------------- ADMIN METHODS --------------------------------------------------------------


    /// FIND ACCOUNT BY ID -> IT, ADMIN
    public Account findById(Long id) {
        return this.accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account not found."));
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
    public SetAccountBanResponseDTO setBanStatusById(Long id, SetAccountBanRequestDTO payload) {
        Account found = findById(id);
        found.setBanned(payload.value());
        this.accountRepository.save(found);
        return new SetAccountBanResponseDTO(found.getId());
    }


}

