package tancredidangelo.capstone.entities.feedActions.follow;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tancredidangelo.capstone.entities.person.account.stack.Account;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "follows")

public class Follow {

    /// attributes

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private Account follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", nullable = false)
    private Account followed;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FollowStatus followStatus;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "response_date")
    private LocalDateTime responseDate;


    /// constructor

    public Follow(Account follower, Account followed) {
        if (follower == followed) { throw new IllegalArgumentException("An account cannot follow itself!"); }
        this.follower= follower;
        this.followed = followed;
        this.followStatus = FollowStatus.PENDING;
        this.requestDate = LocalDateTime.now();
        this.responseDate = null;
    }


    /// to string
    @Override
    public String toString() {
        return "Follow{" +
                "id=" + id +
                ", follower=" + follower +
                ", followed=" + followed +
                ", followStatus=" + followStatus +
                ", requestDate=" + requestDate +
                ", responseDate=" + responseDate +
                '}';
    }
}
