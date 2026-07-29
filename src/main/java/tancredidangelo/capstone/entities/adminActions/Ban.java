package tancredidangelo.capstone.entities.adminActions;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tancredidangelo.capstone.entities.person.account.Account;

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
    private LocalDateTime timestamp;

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
    public Ban(Account account, Account admin, String reason, LocalDateTime startingDate, LocalDateTime expiringDate) {
        this.account = account;
        this.admin = admin;
        this.timestamp = LocalDateTime.now();
        this.reason = reason;
        this.isPermanent = false;
        this.startingDate = startingDate;
        this.expiringDate = expiringDate;
        this.isRevoked = false;
    }

    public Ban(Account account, Account admin, LocalDateTime timestamp, String reason, boolean isPermanent) {
        this.account = account;
        this.admin = admin;
        this.timestamp = timestamp;
        this.reason = reason;
        this.isPermanent = isPermanent;
        this.startingDate = LocalDateTime.now();
        this.expiringDate = null;
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
                ", timestamp=" + timestamp +
                ", isPermanent=" + isPermanent +
                ", reason='" + reason + '\'' +
                ", startingDate=" + (startingDate != null ? startingDate : null) +
                ", expiringDate=" + (expiringDate != null ? expiringDate : null) +
                '}';
    }

}
