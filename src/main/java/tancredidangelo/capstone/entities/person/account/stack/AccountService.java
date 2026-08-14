package tancredidangelo.capstone.entities.person.account.stack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tancredidangelo.capstone.cloudinary.CloudinaryService;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.NewAccountRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.UpdateAccountRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.requests.UpdatePasswordRequestDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.responses.AdminAccountResponseDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.responses.OwnAccountResponseDTO;
import tancredidangelo.capstone.entities.person.account.accountDTOs.responses.PublicAccountResponseDTO;
import tancredidangelo.capstone.entities.person.user.stack.User;
import tancredidangelo.capstone.entities.person.user.stack.UserService;
import tancredidangelo.capstone.entities.tag.Tag;
import tancredidangelo.capstone.entities.tag.TagService;
import tancredidangelo.capstone.exceptions.AlreadyExistsException;
import tancredidangelo.capstone.exceptions.BadRequestException;
import tancredidangelo.capstone.exceptions.NotFoundException;
import tancredidangelo.capstone.exceptions.ValidationException;
import tancredidangelo.capstone.specifications.AccountSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService fileUploader;
    private final TagService tagService;

    public AccountService(AccountRepository accountRepository, UserService userService, PasswordEncoder passwordEncoder, CloudinaryService fileUploader, TagService tagService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.fileUploader = fileUploader;
        this.tagService = tagService;
    }



    // -------------------------- USER METHODS -------------------------------------------------------------------------


    /// CREATE NEW ACCOUNT
    @Transactional
    public OwnAccountResponseDTO save(UUID userId, NewAccountRequestDTO payload) {
        if (this.accountRepository.existsByUsername(payload.username())) {
            throw new AlreadyExistsException("This username is already being used. Please choose another username.");
        }

        User userFound = this.userService.findById(userId);

        String profilePicUrl = null;

        if (payload.file() != null && !payload.file().isEmpty()) {
            profilePicUrl = this.fileUploader.uploadMedia(payload.file(), ("/avatar"));
        }

        Account newAccount = new Account(
                userFound,
                payload.username(),
                passwordEncoder.encode(payload.password()),
                profilePicUrl,
                payload.bio(),
                payload.isPrivate(),
                payload.tags()
        );

        Account saved = this.accountRepository.save(newAccount);
        log.info("Account successfully created with ID {}", saved.getId());

        return OwnAccountResponseDTO.fromEntity(saved);
    }



    /// FIND ACTIVE ACCOUNTS + FILTERS -> ADMIN
    @Transactional(readOnly = true)
    public Page<PublicAccountResponseDTO> searchActiveAccounts(String country, String usernameMatch, List<Long> tagIds, Pageable pageable) {
        List<Tag> tags = new ArrayList<>();

        if (tagIds != null && !tagIds.isEmpty()) {
            tagIds.forEach(tagId -> {
                Tag tag = this.tagService.findById(tagId);
                tags.add(tag);});
        }

        Specification<Account> spec = AccountSpecification.filterActiveAccounts(country, usernameMatch, tags);

        return this.accountRepository.findAll(spec, pageable).map(PublicAccountResponseDTO::fromEntity);
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

        if (payload.bio() != null) found.setBio(payload.bio());
        if (payload.tags() != null) found.setTags(payload.tags());

        return OwnAccountResponseDTO.fromEntity(found);
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
        log.info("Password successfully updated for Account ID {}", id);

        return OwnAccountResponseDTO.fromEntity(found);
    }



    /// UPLOAD PROFILE PICTURE FOR EXISTING ACCOUNT
    @Transactional
    public String updateAvatar(Long accountId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("The uploaded file is empty or missing.");
        }

        Account account = findById(accountId);

        String oldMediaPublicId = this.fileUploader.extractPublicIdFromUrl(account.getProfilePicUrl());
        this.fileUploader.deleteMedia(oldMediaPublicId, "Photo");

        String url = this.fileUploader.uploadMedia(file, ("/avatar"));

        account.setProfilePicUrl(url);
        return url;
    }


    /// DELETE ACCOUNT -> OWNER / ADMIN
    @Transactional
    public void deleteById(Long id) {
        Account found = findById(id);
        this.accountRepository.deleteById(found.getId());
    }

    // ------------------------------- ADMIN / INTERNAL METHODS --------------------------------------------------

    /// FIND ACCOUNT ENTITY BY ID -> INTERNAL, ADMIN
    @Transactional(readOnly = true)
    public Account findById(Long id) {
        return this.accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account with ID " + id + " not found."));
    }


    /// FIND PUBLIC DTO BY ID -> PUBLIC
    @Transactional(readOnly = true)
    public AdminAccountResponseDTO findAccountById(Long id) {
        return AdminAccountResponseDTO.fromEntity(findById(id));
    }


    /// FIND BY USERNAME -> ADMIN, IT
    @Transactional(readOnly = true)
    public Account findByUsername(String username) {
        return this.accountRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Account with username " + username + " not found."));
    }


    /// FIND ACCOUNTS BY USER ID
    @Transactional(readOnly = true)
    public List<AdminAccountResponseDTO> findByUserId(UUID userId) {
        return this.accountRepository.findByUserId(userId).stream()
                .map(AdminAccountResponseDTO::fromEntity)
                .toList();
    }


    /// EXISTS BY USERNAME -> IT, ADMIN
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return this.accountRepository.existsByUsername(username);
    }


    /// FIND BANNED ACCOUNTS -> ADMIN
    @Transactional(readOnly = true)
    public Page<AdminAccountResponseDTO> searchBannedAccounts(String country, String usernameMatch, Boolean isBanned, Pageable pageable) {
        Specification<Account> spec = AccountSpecification.filterAccounts(country, usernameMatch, isBanned);
        return this.accountRepository.findAll(spec, pageable).map(AdminAccountResponseDTO::fromEntity);
    }


}