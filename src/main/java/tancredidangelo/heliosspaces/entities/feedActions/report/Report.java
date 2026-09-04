package tancredidangelo.heliosspaces.entities.feedActions.report;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.post.Post;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "reports")
public class Report {

    /// attributes

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Account author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_account_id", nullable = false)
    private Account reportedAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_post_id")
    private Post reportedPost;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private ReportStatus status;



    /// constructor

    // report account
    public Report(Account author, Account reportedAccount, String reason) {
        this.author = author;
        this.reportedAccount = reportedAccount;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
        this.status = ReportStatus.PENDING;
    }


    // report post
    public Report(Account author, Post reportedPost, String reason) {
        this.author = author;
        this.reportedPost = reportedPost;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
        this.status = ReportStatus.PENDING;
    }



    /// to string

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", author=" + author +
                ", reportedAccount=" + reportedAccount +
                ", reason='" + reason + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
