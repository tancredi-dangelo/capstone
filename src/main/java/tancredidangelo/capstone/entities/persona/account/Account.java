package tancredidangelo.capstone.entities.persona.account;

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
import tancredidangelo.capstone.entities.persona.user.User;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.SavedPost;

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

    @Column(name = "is_banned", nullable = false)
    private boolean isBanned;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountRoles role;

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
    public Account(User user, String username, String password, List<String> tags) {
        this.user = user;
        this.username = username;
        this.password = password;
        this.profilePicUrl = "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y";
        this.isBanned = false;
        this.role = AccountRoles.USER;
        this.tags = tags;
        this.dateOfRegistration = LocalDateTime.now();
    }



    /// authorities and details

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isBanned;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
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
