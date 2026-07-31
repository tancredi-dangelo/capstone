package tancredidangelo.capstone.entities.person.account.accountDTOs;

import jakarta.validation.constraints.NotBlank;

public record SetAccountBanRequestDTO(@NotBlank boolean value) {
}
