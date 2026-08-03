package tancredidangelo.capstone.entities.post.postSubclasses.carousel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.post.Post;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "carousels")

public class Carousel extends Post {

    /// attributes

    @ElementCollection
    @CollectionTable(name = "carousel_media", joinColumns = @JoinColumn(name = "carousel_id"))
    @Column(name = "media_url", nullable = false)
    private List<String> mediaUrls = new ArrayList<>();

    @Column(nullable = false)
    private int length;


    /// constructor
    public Carousel(Account author, String caption, List<String> mediaUrls) {
        super(author, caption);
        this.mediaUrls = mediaUrls;
        this.length = mediaUrls.size();
    }

    @Override
    public String toString() {
        return "Carousel{" +
                "id=" + getId() +
                ", author=" + (getAuthor() != null ? getAuthor() : null) +
                ", caption='" + (getCaption() != null ? getCaption() : null) + '\'' +
                ", likes=" + getLikes().size() +
                ", comments=" + getComments().size() +
                ", mediaUrls=" + mediaUrls +
                ", length=" + length +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
