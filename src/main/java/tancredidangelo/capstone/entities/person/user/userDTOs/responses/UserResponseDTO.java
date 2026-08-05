package tancredidangelo.capstone.entities.person.user.userDTOs.responses;

import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.user.stack.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        LocalDate birthdate,
        String country,
        List<Long> userAccounts
) {

    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getBirthdate(),
                user.getCountry(),
                user.getUserAccounts().stream().map(Account::getId).toList()
        );
    }
}
