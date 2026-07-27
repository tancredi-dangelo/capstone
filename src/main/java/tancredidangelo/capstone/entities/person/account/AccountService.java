package tancredidangelo.capstone.entities.person.account;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountService {

    /// dependency injection
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    /// methods

    // findById
    public Account findById(Long id) {
        return this.accountRepository.findById(id).orElseThrow()
    }
}
