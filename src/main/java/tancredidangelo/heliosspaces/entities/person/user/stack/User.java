package tancredidangelo.heliosspaces.entities.person.user.stack;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "users")
public class User {

    /// attributes

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(name = "country_code", nullable = false, length = 2)
    private String country;

    @Column(name = "is_flagged", nullable = false)
    private boolean isFlagged;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> userAccounts = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime dateOfRegistration;



    /// constructor
    public User(String firstName, String lastName, String email, LocalDate birthdate, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthdate = birthdate;
        this.country = country;
        this.isFlagged = false;
        this.dateOfRegistration = LocalDateTime.now();
    }



    /// to string
    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", birthdate=" + birthdate +
                ", country='" + country + '\'' +
                ", isFlagged=" + isFlagged +
                ", dateOfRegistration=" + dateOfRegistration +
                '}';
    }
}
