package tancredidangelo.capstone.entities.adminActions.ban;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tancredidangelo.capstone.entities.person.account.stack.Account;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "banned_accounts")
public class Ban {

    /// attributes
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Account admin;

    @Column(nullable = false)
    private String reason;

    @Column(name = "is_permanent")
    private boolean isPermanent;

    @Column(name = "starting_date")
    private LocalDateTime startingDate;

    @Column(name = "expiring_date")
    private LocalDateTime expiringDate;

    @Column(name = "is_revoked", nullable = false)
    private boolean isRevoked;




    /// constructor
    public Ban(Account account, Account admin, String reason, boolean isPermanent, LocalDateTime expiringDate) {
        this.account = account;
        this.admin = admin;
        this.reason = reason;
        this.startingDate = LocalDateTime.now();
        this.isPermanent = isPermanent;
        this.expiringDate = this.isPermanent ? null : expiringDate;
        this.isRevoked = false;
    }



    /// methods

    public boolean isActive() {
        if (this.isRevoked) { return false; }

        if (this.isPermanent) { return true; }

        LocalDateTime now = LocalDateTime.now();
        if (!now.isBefore(this.startingDate) && now.isBefore(this.expiringDate)) {
            return true;
        } else { return false; }
    }



    /// to string

    @Override
    public String toString() {
        return "Ban{" +
                "id=" + id +
                ", accountId=" + (account != null ? account.getId() : null) +
                ", adminId=" + (admin != null ? admin.getId() : null) +
                ", isPermanent=" + isPermanent +
                ", reason='" + reason + '\'' +
                ", startingDate=" + (startingDate != null ? startingDate : null) +
                ", expiringDate=" + (expiringDate != null ? expiringDate : null) +
                '}';
    }

}
