package tancredidangelo.heliosspaces.entities.feedActions.comment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.feedActions.like.Like;
import tancredidangelo.heliosspaces.entities.post.Post;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "comments")
public class Comment {

    /// attributes
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Account author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @OneToMany(mappedBy = "comment")
    @Setter(AccessLevel.NONE)
    private List<Like> likes;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime timestamp;


    /// constructor

    public Comment(Account author, Post post, String text) {
        this.author = author;
        this.post = post;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }


    /// helper methods

    public void addLike(Like like) {
        this.likes.add(like);
        like.setComment(this);
    }


    public void removeLike(Like like) {
        this.likes.remove(like);
        like.setComment(null);
    }


    /// to string

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", author=" + (author != null ? author.getId() : null) +
                ", postId=" + (post != null ? post.getId() : null) +
                ", text=" + text +
                ", timestamp=" + timestamp +
                '}';
    }



}
