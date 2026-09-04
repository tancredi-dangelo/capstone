package tancredidangelo.heliosspaces.entities.person.user.stack;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import tancredidangelo.heliosspaces.emailSender.EmailSender;
import tancredidangelo.heliosspaces.entities.person.user.userDTOs.requests.UpdateEmailRequestDTO;
import tancredidangelo.heliosspaces.entities.person.user.userDTOs.requests.UpdateFlagRequestDTO;
import tancredidangelo.heliosspaces.entities.person.user.userDTOs.requests.UpdateUserRequestDTO;
import tancredidangelo.heliosspaces.entities.person.user.userDTOs.responses.UserResponseDTO;
import tancredidangelo.heliosspaces.exceptions.AlreadyExistsException;
import tancredidangelo.heliosspaces.exceptions.NotFoundException;
import tancredidangelo.heliosspaces.exceptions.ValidationException;
import tancredidangelo.heliosspaces.helpers.CountryCodeConverter;
import tancredidangelo.heliosspaces.specifications.UserSpecification;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j

@Service
public class UserService {

    /// dependency injection
    private final UserRepository userRepository;
    private final EmailSender emailSender;

    public UserService(UserRepository userRepository, EmailSender emailSender) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }


    // ------------- USER METHODS --------------------------------------------------


    /// SAVE NEW USER
    @Transactional
    public UserResponseDTO save(User user) {
        User saved = this.userRepository.save(user);
        return UserResponseDTO.fromEntity(saved);
    }



    public UserResponseDTO getOwnUserDetails(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User non trovato"));

        return UserResponseDTO.fromEntity(user);
    }


    /// UPDATE USER DETAILS -> ONLY USER
    @Transactional
    public UserResponseDTO updateById(UUID id, UpdateUserRequestDTO payload) {
        User found = findById(id);

        found.setFirstName(payload.firstName());
        found.setLastName(payload.lastName());
        found.setBirthdate(payload.birthdate());
        found.setCountry(CountryCodeConverter.toIsoCode(payload.country()));

        User updated = this.userRepository.save(found);
        log.info("User updated.");

        return UserResponseDTO.fromEntity(updated);
    }


    /// UPDATE USER EMAIL -> ONLY USER
    @Transactional
    public UserResponseDTO updateEmailById(UUID id, UpdateEmailRequestDTO payload) {

        User found = findById(id);

        String oldEmail = found.getEmail();

        if (!payload.email().equals(oldEmail)) {

            if (this.userRepository.existsByEmail(payload.email())) {
                throw new AlreadyExistsException("An account with this email already exists.");
            }
        }

        found.setEmail(payload.email());
        log.info("Email updated");

        User updated = this.userRepository.save(found);

        this.emailSender.sendRegistrationEmail(updated);

        return UserResponseDTO.fromEntity(updated);
    }



    // ------------ ADMIN / IT METHODS ----------------------------------------------------------------


    /// SEARCH AND FILTER ALL USERS -> ONLY ADMIN
    public Page<UserResponseDTO> searchUsers(Boolean isFlagged, String emailMatch, String country, LocalDate birthdate, Pageable pageable) {
        Specification<User> spec = UserSpecification.filterUsers(country, emailMatch, birthdate, isFlagged);
        Page<User> rawUsers = this.userRepository.findAll(spec, pageable);
        return rawUsers.map(UserResponseDTO::fromEntity);
    }


    /// FIND USER BY ID -> ADMIN, IT
    public User findById(UUID id) {
        return this.userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found."));
    }


    /// FLAG USER
    @Transactional
    public UserResponseDTO setFlagById(UUID id, UpdateFlagRequestDTO payload) {

        User found = findById(id);

        if (found.isFlagged() && payload.flagValue()) {
            throw new ValidationException("This user is already flagged.");
        } else if (!found.isFlagged() && !payload.flagValue()) {
            throw new ValidationException("This user is already unflagged.");
        }

        found.setFlagged(payload.flagValue());

        User updated = this.userRepository.save(found);

        return UserResponseDTO.fromEntity(updated);

    }



    /// DELETE USER BY ID -> ONLY ADMIN
    @Transactional
    public void deleteById(UUID id) {
        this.userRepository.deleteById(id);
        log.info("User deleted.");
    }


    /// DELETE ALL USERS -> ONLY ADMIN
    @Transactional
    public void deleteAll() {
        this.userRepository.deleteAll();
    }

}
