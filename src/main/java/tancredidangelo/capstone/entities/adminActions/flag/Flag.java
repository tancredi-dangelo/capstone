package tancredidangelo.capstone.entities.adminActions.flag;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.user.stack.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "flagged_users")
public class Flag {

    /// attributes
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Account admin;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String reason;



    /// constructor
    public Flag(User user, Account admin, String reason) {
        this.user = user;
        this.admin = admin;
        this.timestamp = LocalDateTime.now();
        this.reason = reason;
    }


    /// to string
    @Override
    public String toString() {
        return "Flag{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", adminId=" + (admin != null ? admin.getId() : null) +
                ", timestamp=" + timestamp +
                ", reason='" + reason + '\'' +
                '}';
    }
}
