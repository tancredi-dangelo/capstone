package tancredidangelo.capstone.entities.feedActions.like;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.feedActions.comment.Comment;
import tancredidangelo.capstone.entities.post.Post;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor


@Entity
@Table(name = "likes")
public class Like {

    /// attributes
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Account author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(nullable = false)
    private LocalDateTime timestamp;


    /// constructor Like -> Post

    public Like(Account author, Post postId) {
        this.author = author;
        this.post = postId;
        this.timestamp = LocalDateTime.now();
    }

    /// constructor Like -> Comment

    public Like(Account author, Comment comment) {
        this.author = author;
        this.comment = comment;
        this.timestamp = LocalDateTime.now();
    }


    /// to string
    @Override
    public String toString() {
        return "Like{" +
                "id=" + id +
                ", author=" + (author != null ? author.getId() : null) +
                ", postId=" + (post != null ? post.getId() : null) +
                ", commentId=" + (comment != null ? comment.getId() : null) +
                ", timestamp=" + timestamp +
                '}';
    }
}
