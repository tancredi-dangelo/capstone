package tancredidangelo.capstone.entities.postSubclasses.video;

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
@Table(name = "videos")

public class Video extends Post {

    /// attributes

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds;


    /// constructor
    public Video(Account author, String caption, String videoUrl, int durationSeconds) {
        super(author, caption);
        this.videoUrl = videoUrl;
        this.durationSeconds = durationSeconds;
    }

    /// to string
    @Override
    public String toString() {
        return "Video{" +
                "id=" + getId() +
                ", author=" + getAuthor() +
                ", caption='" + (getCaption() != null ? getCaption() : null) + '\'' +
                ", likes=" + getLikes().size() +
                ", comments=" + getComments().size() +
                ", videoUrl='" + videoUrl + '\'' +
                ", durationSeconds=" + durationSeconds +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
