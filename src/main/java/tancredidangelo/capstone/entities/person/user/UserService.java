package tancredidangelo.capstone.entities.person.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.person.user.userDTOs.*;
import tancredidangelo.capstone.exceptions.AlreadyExistsException;
import tancredidangelo.capstone.exceptions.NotFoundException;

import java.util.UUID;

@Slf4j

@Service
public class UserService {

    /// dependency injection
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /// methods

    /// REGISTER NEW USER -> ONLY USER
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


    public User findById(UUID id) {
        return this.userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found."));
    }


    /// CHECK IF USER EXISTS BY EMAIL -> IT
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }


    /// CHECK IF USER IS FLAGGED -> ONLY ADMIN
    public boolean existsByEmailAndIsFlaggedTrue(String email) {
        return this.userRepository.existsByEmailAndIsFlaggedTrue(email);
    }


    /// FIND USER BY EMAIL -> IT
    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(()-> new NotFoundException("User not found."));
    }


    /// FIND FLAGGED USERS -> ONLY ADMIN
    public Page<User> findByIsFlaggedTrue(Pageable pageable) {
        return this.userRepository.findByIsFlaggedTrue(pageable);
    }



    // todo : find by country and is flagged true


    /// UPDATE USER DETAILS -> ONLY USER
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
    public UpdateEmailResponseDTO updateEmailById(UUID id, UpdateEmailRequestDTO payload) {

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
        return new UpdateEmailResponseDTO(updated.getId());
    }


    /// DELETE USER BY ID -> ONLY ADMIN
    public void deleteById(UUID id) {

        User found = findById(id);

        this.userRepository.deleteById(id);
        log.info("User deleted.");
    }


    /// DELETE ALL USERS -> ONLY ADMIN
    public void deleteAll() {
        this.userRepository.deleteAll();
    }

}
