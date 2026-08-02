package tancredidangelo.capstone.entities.postSubclasses;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.post.Post;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "photos")

public class Photo extends Post {

    /// attributes

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;



    /// constructor

    public Photo(Account author, String caption, String photoUrl) {
        super(author, caption);
        this.photoUrl = photoUrl;
    }



    /// to string

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + getId() +
                ", author=" + getAuthor() +
                ", caption='" + (getCaption() != null ? getCaption() : null) + '\'' +
                ", likes=" + getLikes().size() +
                ", comments=" + getComments().size() +
                ", photoUrl='" + photoUrl + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }

}
