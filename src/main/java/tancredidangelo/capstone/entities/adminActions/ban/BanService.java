package tancredidangelo.capstone.entities.adminActions.ban;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.adminActions.ban.banDTO.response.BanResponseDTO;
import tancredidangelo.capstone.entities.adminActions.ban.banDTO.request.TemporaryBanRequestDTO;
import tancredidangelo.capstone.entities.adminActions.ban.banDTO.request.PermanentBanRequestDTO;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.account.stack.AccountRepository;
import tancredidangelo.capstone.entities.person.account.stack.AccountService;
import tancredidangelo.capstone.exceptions.BadRequestException;
import tancredidangelo.capstone.exceptions.NotFoundException;

import java.time.LocalDateTime;

@Service
@Slf4j
public class BanService {

    /// dependency injection

    private final BanRepository banRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public BanService(BanRepository banRepository, AccountService accountService, AccountRepository accountRepository) {
        this.banRepository = banRepository;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }


    // methods



    /// CREATE TEMPORARY BAN
    @Transactional
    public BanResponseDTO createTemporaryBan(TemporaryBanRequestDTO payload, Authentication authentication) {

        Account targetAccount = accountService.findById(payload.accountId());
        Account adminAccount = (Account) authentication.getPrincipal();

        targetAccount.setIsBanned(true);
        this.accountRepository.save(targetAccount);

        Ban newBan = new Ban(targetAccount, adminAccount, payload.reason(), false, payload.expiringDate());
        Ban saved = this.banRepository.save(newBan);

        log.info("Ban ID {} applied to Account ID {} by Admin ID {}", saved.getId(), targetAccount.getId(), adminAccount.getId());

        return BanResponseDTO.fromEntity(saved);
    }



    /// CREATE PERMANENT BAN
    @Transactional
    public BanResponseDTO createPermanentBan(PermanentBanRequestDTO payload, Authentication authentication) {

        Account targetAccount = accountService.findById(payload.accountId());
        Account adminAccount = (Account) authentication.getPrincipal();

        targetAccount.setIsBanned(true);
        this.accountRepository.save(targetAccount);

        Ban newBan = new Ban(targetAccount, adminAccount, payload.reason(), true, null);
        Ban saved = this.banRepository.save(newBan);

        log.info("Ban ID {} applied to Account ID {} by Admin ID {}", saved.getId(), targetAccount.getId(), adminAccount.getId());

        return BanResponseDTO.fromEntity(saved);

    }



    /// REVOKE BAN
    @Transactional
    public BanResponseDTO revokeBan(Long banId) {

        Ban ban = banRepository.findById(banId)
                .orElseThrow(() -> new NotFoundException("Ban record not found"));

        Account account = ban.getAccount();

        if (ban.isRevoked()) {
            throw new BadRequestException("Ban is already revoked.");
        }

        if (!ban.isActive()) {
            throw new BadRequestException("This ban is no active anymore. Impossible to revoke it.");
        }

        ban.setRevoked(true);

        account.setIsBanned(true);
        this.accountRepository.save(account);

        Ban updated = banRepository.save(ban);

        log.info("Ban ID {} has been revoked", banId);

        return BanResponseDTO.fromEntity(updated);
    }



    public Page<BanResponseDTO> getBansByAccount(Long accountId, Pageable pageable) {
        return banRepository.findByAccountId(accountId, pageable).map(BanResponseDTO::fromEntity);
    }



    public BanResponseDTO getBanById(Long id) {
        Ban ban = banRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ban record not found"));
        return BanResponseDTO.fromEntity(ban);
    }
}