package tancredidangelo.capstone.entities.savedPost;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.post.Post;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "saved_posts", uniqueConstraints = {@UniqueConstraint(columnNames = {"account_id", "post_id"})})

public class SavedPost {

    /// attributes

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private LocalDateTime timestamp;



    /// constructor

    public SavedPost(Account account, Post post) {
        this.account = account;
        this.post = post;
        this.timestamp = LocalDateTime.now();
    }


    /// to string

    @Override
    public String toString() {
        return "SavedPost{" +
                "id=" + id +
                ", accountId=" + (account != null ? account.getId() : null) +
                ", postId=" + (post != null ? post.getId() : null) +
                ", timestamp=" + timestamp +
                '}';
    }
}
