package tancredidangelo.heliosspaces.entities.post.postSubclasses.video;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tancredidangelo.heliosspaces.cloudinary.CloudinaryService;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.person.account.stack.AccountService;
import tancredidangelo.heliosspaces.entities.post.Post;
import tancredidangelo.heliosspaces.entities.post.PostService;
import tancredidangelo.heliosspaces.entities.post.postDTO.requests.create.CreateVideoRequestDTO;
import tancredidangelo.heliosspaces.entities.post.postDTO.responses.VideoResponseDTO;


@Service
@Slf4j
public class VideoService {

    private final PostService postService;
    private final AccountService accountService;
    private final CloudinaryService cloudinaryService;

    public VideoService(PostService postService, AccountService accountService, CloudinaryService cloudinaryService) {
        this.postService = postService;
        this.accountService = accountService;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    public VideoResponseDTO createVideo(Long authorId, CreateVideoRequestDTO payload) {

        Account author = this.accountService.findById(authorId);

        String videoUrl = this.cloudinaryService.uploadMedia(payload.file(), "posts/videos");

        Video newVideo = new Video(author, payload.caption(), videoUrl, payload.durationSeconds());
        Post saved = this.postService.save(newVideo);

        log.info("Post type:'Video' created with ID: {}", saved.getId());

        return VideoResponseDTO.fromEntity((Video) saved);
    }
}