package tancredidangelo.capstone.entities.person.account;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.person.account.accountDTOs.*;
import tancredidangelo.capstone.exceptions.AlreadyExistsException;
import tancredidangelo.capstone.exceptions.NotFoundException;
import tancredidangelo.capstone.exceptions.ValidationException;
import tancredidangelo.capstone.specifications.AccountSpecification;

import java.util.List;

@Service
@Slf4j
public class AccountService {

    /// dependency injection
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    /// -------------------------- USER METHODS -------------------------------------------------------------------------


    /// REGISTER NEW ACCOUNT -> ONLY USER
    @Transactional
    public Long save(NewAccountRequestDTO payload) {
        if (this.accountRepository.existsByUsername(payload.username())) {
            throw new AlreadyExistsException("This username is already being used. Please choose another username.");
        }

        Account newAccount = new Account(payload.user(), payload.username(), payload.password(), payload.tags());
        Account saved = this.accountRepository.save(newAccount);
        log.info("Account registered with ID: {}.", saved.getId());
        return saved.getId();
    }


    /// FIND BY USERNAME -> ADMIN, IT
    public Account findByUsername(String username) {
        return this.accountRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Account not found."));
    }


    /// FIND ACTIVE ACCOUNTS -> USER (+ADMIN)
    public Page<Account> searchActiveAccounts(String country, String usernameMatch, List<String> tags, Pageable pageable) {
        Specification<Account> spec = AccountSpecification.filterActiveAccounts(country, usernameMatch, tags);
        return this.accountRepository.findAll(spec, pageable);
    }


    /// UPDATE ACCOUNT -> USER (+ADMIN)
    public UpdateAccountResponseDTO updateById(Long id, UpdateAccountRequestDTO payload) {
        Account found = findById(id);

        found.setUsername(payload.username());
        found.setTags(payload.tags());

        Account saved = this.accountRepository.save(found);
        return new UpdateAccountResponseDTO(saved.getId());
    }


    /// UPDATE PASSWORD -> ONLY USER
    @Transactional
    public UpdatePasswordResponseDTO updatePasswordById(Long id, UpdatePasswordRequestDTO payload) {

        Account found = findById(id);

        if (!payload.oldPassword().equals(found.getPassword())) {
            throw new ValidationException("Old password is not matching. Please try again.");
        }

        if (payload.newPassword().equals(found.getPassword())) {
            throw new ValidationException("New password must be different from the old one!");
        }

        found.setPassword(payload.newPassword());
        Account updated = this.accountRepository.save(found);
        log.info("Password successfully updated");

        return new UpdatePasswordResponseDTO(updated.getId());
    }


    /// DELETE ACCOUNT -> ONLY USER
    @Transactional
    public void deleteById(Long id) {
        Account found = findById(id);
        this.accountRepository.deleteById(id);
    }



    /// ------------------------------- ADMIN METHODS --------------------------------------------------------------

    /// FIND ACCOUNT BY ID -> IT, ADMIN
    public Account findById(Long id) {
        return this.accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account not found."));
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
    public Long setBanStatusById(Long id, boolean isBanned) {
        Account found = findById(id);
        found.setBanned(isBanned);
        return found.getId();
    }


}

