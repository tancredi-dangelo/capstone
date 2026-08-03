package tancredidangelo.capstone.entities.person.account.stack;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tancredidangelo.capstone.entities.feedActions.follow.Follow;
import tancredidangelo.capstone.entities.person.account.AccountRoles;
import tancredidangelo.capstone.entities.person.user.stack.User;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.savedPost.SavedPost;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "accounts")
public class Account implements UserDetails {

    /// attributes

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "profile_picture_url")
    private String profilePicUrl;

    @Column(name = "bio")
    private String bio;

    @Column(name = "is_private")
    private boolean isPrivate;

    @Column(name = "is_banned", nullable = false)
    private boolean isBanned;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountRoles role = AccountRoles.USER;

    @ElementCollection
    @CollectionTable(name = "account_tags", joinColumns = @JoinColumn(name = "account_id"))
    private List<String> tags = new ArrayList<>();

    @Column(name = "date_of_registration")
    private LocalDateTime dateOfRegistration;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<Follow> following = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<SavedPost> savedPosts = new ArrayList<>();



    /// constructor

    public Account(User user, String username, String password, String profilePicUrl, String bio, boolean isPrivate, List<String> tags) {
        this.user = user;
        this.username = username;
        this.password = password;
        this.profilePicUrl = profilePicUrl == null ? "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y" : profilePicUrl;
        this.bio = bio;
        this.isPrivate = isPrivate;
        this.isBanned = false;
        this.tags = (tags == null) ? new ArrayList<>() : tags;
        this.dateOfRegistration = LocalDateTime.now();
    }


    /// admin constructor

    public Account(User user, String username, String password) {
        this.user = user;
        this.username = username;
        this.password = password;
        this.profilePicUrl = null;
        this.bio = null;
        this.isBanned = false;
        this.role = AccountRoles.ADMIN;
        this.tags = null;
        this.dateOfRegistration = LocalDateTime.now();
        this.posts = null;
        this.followers = null;
        this.following = null;
        this.savedPosts = null;
    }



    /// authorities and details

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }


    @Override
    public boolean isAccountNonLocked() {
        return !this.isBanned;
    }



    /// to string

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", profilePicUrl='" + profilePicUrl + '\'' +
                ", isBanned=" + isBanned +
                ", tags=" + tags +
                ", id=" + id +
                ", dateOfRegistration=" + dateOfRegistration +
                '}';
    }



}
