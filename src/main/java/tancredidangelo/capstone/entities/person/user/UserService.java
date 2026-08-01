package tancredidangelo.capstone.entities.person.user;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import tancredidangelo.capstone.entities.person.user.userDTOs.*;
import tancredidangelo.capstone.exceptions.AlreadyExistsException;
import tancredidangelo.capstone.exceptions.NotFoundException;
import tancredidangelo.capstone.exceptions.ValidationException;
import tancredidangelo.capstone.helpers.CountryCodeConverter;
import tancredidangelo.capstone.helpers.CountryCodeConverter.*;
import tancredidangelo.capstone.specifications.UserSpecification;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j

@Service
public class UserService {

    /// dependency injection
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // ------------- USER METHODS --------------------------------------------------


    /// SAVE NEW USER
    @Transactional
    public User save(User user) {
        return this.userRepository.save(user);
    }


    /// UPDATE USER DETAILS -> ONLY USER
    @Transactional
    public UpdateUserResponseDTO updateById(UUID id, UpdateUserRequestDTO payload) {
        User found = findById(id);

        found.setFirstName(payload.firstName());
        found.setLastName(payload.lastName());
        found.setBirthdate(payload.birthdate());
        found.setCountry(CountryCodeConverter.toIsoCode(payload.country()));

        User updated = this.userRepository.save(found);
        log.info("User updated.");
        return new UpdateUserResponseDTO(updated.getId());
    }


    /// UPDATE USER EMAIL -> ONLY USER
    @Transactional
    public UpdateEmailResponseDTO updateEmailById(UUID id, UpdateEmailRequestDTO payload) {

        User found = findById(id);

        String oldEmail = found.getEmail();

        if (!payload.email().equals(oldEmail)) {

            if (this.userRepository.existsByEmail(payload.email())) {
                throw new AlreadyExistsException("An account with this email already exists.");
            }
        }

        // TODO: SEND CONFIRMATION EMAIL TO NEW EMAIL ADDRESS

        found.setEmail(payload.email());
        log.info("Email updated");

        User updated = this.userRepository.save(found);
        return new UpdateEmailResponseDTO(updated.getId());
    }



    // ------------ ADMIN / IT METHODS ----------------------------------------------------------------


    /// SEARCH AND FILTER ALL USERS -> ONLY ADMIN
    public Page<User> searchUsers(Boolean isFlagged, String emailMatch, String country, LocalDate birthdate, Pageable pageable) {
        Specification<User> spec = UserSpecification.filterUsers(country, emailMatch, birthdate, isFlagged);
        return this.userRepository.findAll(spec, pageable);
    }


    /// FIND USER BY ID -> ADMIN, IT
    public User findById(UUID id) {
        return this.userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found."));
    }


    /// FIND USER BY EMAIL -> ADMIN
    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(()-> new NotFoundException("User not found."));
    }


    /// CHECK IF USER EXISTS BY EMAIL -> IT
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }


    /// FLAG USER
    @Transactional
    public UpdateFlagResponseDTO setFlagById(UUID id, boolean flagValue) {

        User found = findById(id);

        if (found.isFlagged() && flagValue) {
            throw new ValidationException("This user is already flagged.");
        } else if (!found.isFlagged() && !flagValue) {
            throw new ValidationException("This user is already unflagged.");
        }

        found.setFlagged(flagValue);
        this.userRepository.save(found);
        return new UpdateFlagResponseDTO(found.getId());

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
