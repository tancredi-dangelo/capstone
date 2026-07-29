package tancredidangelo.capstone.entities.person.user;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.person.account.Account;
import tancredidangelo.capstone.entities.person.user.userDTOs.*;
import tancredidangelo.capstone.exceptions.AlreadyExistsException;
import tancredidangelo.capstone.exceptions.NotFoundException;
import tancredidangelo.capstone.specifications.AccountSpecification;
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


    /// ------------- USER METHODS --------------------------------------------------

    /// REGISTER NEW USER -> ONLY USER
    @Transactional
    public NewUserResponseDTO save(NewUserRequestDTO payload) {

        if (this.userRepository.existsByEmail(payload.email())) {
            throw new AlreadyExistsException("An account with this email already exists.");
        }

        User newUser = new User();
        newUser.setFirstName(payload.firstName());
        newUser.setLastName(payload.lastName());
        newUser.setEmail(payload.email());
        newUser.setBirthdate(payload.birthdate());
        newUser.setCountry(payload.country());

        User saved = this.userRepository.save(newUser);
        return new NewUserResponseDTO(saved.getId());
    }


    /// UPDATE USER DETAILS -> ONLY USER
    @Transactional
    public UpdateUserResponseDTO updateById(UUID id, UpdateUserRequestDTO payload) {
        User found = findById(id);

        found.setFirstName(payload.firstName());
        found.setLastName(payload.lastName());
        found.setBirthdate(payload.birthdate());
        found.setCountry(payload.country());

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



    /// ------------ ADMIN / IT METHODS ----------------------------------------------------------------


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
    public UUID flagUserById(UUID id) {
        User found = findById(id);
        if (found.isFlagged()) {
            throw new RuntimeException("This user is already flagged.");
        } else {
            found.setFlagged(true);
            this.userRepository.save(found);
            return found.getId();
        }
    }


    /// UNFLAG USER
    @Transactional
    public UUID unflagUserById(UUID id) {
        User found = findById(id);
        if (!found.isFlagged()) {
            throw new RuntimeException("This user is already flagged.");
        } else {
            found.setFlagged(false);
            this.userRepository.save(found);
            return found.getId();
        }

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
