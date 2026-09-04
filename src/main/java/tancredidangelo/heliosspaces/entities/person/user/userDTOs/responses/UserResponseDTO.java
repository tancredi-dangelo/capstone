package tancredidangelo.heliosspaces.entities.person.user.userDTOs.responses;

import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.person.user.stack.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        LocalDate birthdate,
        String country,
        List<Long> userAccounts,
        LocalDateTime dateOfRegistration
) {

    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getBirthdate(),
                user.getCountry(),
                user.getUserAccounts().stream().map(Account::getId).toList(),
                user.getDateOfRegistration()
        );
    }
}
