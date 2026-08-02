package tancredidangelo.capstone.entities.postSubclasses.writing;

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
@Table(name = "written_posts")
public class Writing extends Post {

    /// attributes

    @Column(nullable = false)
    private String text;



    /// constructor

    public Writing(Account author, String text) {
        super(author);
        this.text = text;
    }


    /// to string
    @Override
    public String toString() {
        return "Writing{" +
                "id=" + getId() +
                ", author=" + getAuthor() +
                ", text='" + text + '\'' +
                ", likes=" + getLikes().size() +
                ", comments=" + getComments().size() +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
