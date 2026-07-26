package tancredidangelo.capstone.entities.feedActions.notification;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tancredidangelo.capstone.entities.feedActions.comment.Comment;
import tancredidangelo.capstone.entities.feedActions.follow.Follow;
import tancredidangelo.capstone.entities.person.account.Account;
import tancredidangelo.capstone.entities.post.Post;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "notifications")
public class Notification {

    /// attributes

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private Account recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regarding_post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regarding_comment_id")
    private Comment comment;

    @Column(name = "comment_text")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_id")
    private Follow follow;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "is_read")
    private boolean isRead;



    /// constructor LIKE TO POST
    public Notification(Account sender, Account recipient, Post post) {
        this.notificationType = NotificationType.LIKE_TO_POST;
        this.sender = sender;
        this.recipient = recipient;
        this.post = post;
        this.comment = null;
        this.text = null;
        this.follow = null;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    /// constructor LIKE TO COMMENT
    public Notification(Account sender, Account recipient, Comment comment) {
        this.notificationType = NotificationType.LIKE_TO_COMMENT;
        this.sender = sender;
        this.recipient = recipient;
        this.post = null;
        this.comment = comment;
        this.text = null;
        this.follow = null;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }


    /// constructor COMMENT TO POST
    public Notification(Account sender, Account recipient, Post post, String text) {
        this.notificationType = NotificationType.COMMENT;
        this.sender = sender;
        this.recipient = recipient;
        this.post = post;
        this.comment = null;
        this.text = text;
        this.follow = null;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }


    /// constructor FOLLOW
    public Notification(Account sender, Account recipient, Follow follow) {
        this.notificationType = NotificationType.FOLLOW;
        this.sender = sender;
        this.recipient = recipient;
        this.post = null;
        this.comment = null;
        this.text = null;
        this.follow = follow;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }



    /// to string

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", type=" + notificationType +
                ", senderId=" + (sender != null ? sender.getId() : null) +
                ", recipientId=" + (recipient != null ? recipient.getId() : null) +
                ", isRead=" + isRead +
                ", timestamp=" + timestamp +
                '}';

    }
}
