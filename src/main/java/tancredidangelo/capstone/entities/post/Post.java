package tancredidangelo.capstone.entities.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.feedActions.comment.Comment;
import tancredidangelo.capstone.entities.feedActions.like.Like;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "posts")
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class Post {

    /// attributes
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Account author;

    @Column
    private String caption;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "was_updated")
    private boolean isUpdated;

    @OneToMany(mappedBy = "post")
    @Setter(AccessLevel.NONE)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    @Setter(AccessLevel.NONE)
    private List<Comment> comments = new ArrayList<>();


    /// constructor

    public Post(Account author, String caption) {
        this.author = author;
        this.caption = caption;
        this.timestamp = LocalDateTime.now();
        this.isUpdated = false;
    }

    public Post(Account author) {
        this.author = author;
        this.timestamp = LocalDateTime.now();
        this.isUpdated = false;
    }



    /// helper methods LIKE

    public void addLike(Like like) {
        this.likes.add(like);
        like.setPost(this);
    }

    public void removeLike(Like like) {
        this.likes.remove(like);
        like.setPost(null);
    }


    /// helper methods COMMENT

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setPost(null);
    }


    /// to string

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", author=" + author.getId() +
                ", caption='" + caption +
                ", timestamp=" + timestamp +
                ", likes=" + likes.size() +
                ", comments=" + comments.size() +
                ", isUpdated=" + isUpdated +
                '}';
    }
}
