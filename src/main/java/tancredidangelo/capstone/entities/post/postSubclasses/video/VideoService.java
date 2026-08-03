package tancredidangelo.capstone.entities.post.postSubclasses.video;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.account.stack.AccountService;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.PostService;
import tancredidangelo.capstone.entities.post.postDTO.requests.create.CreateVideoRequestDTO;
import tancredidangelo.capstone.entities.post.postDTO.responses.VideoResponseDTO;

@Service
@Slf4j
public class VideoService {

    /// dependency injection

    private final PostService postService;
    private final AccountService accountService;

    public VideoService(PostService postService, AccountService accountService) {
        this.postService = postService;
        this.accountService = accountService;
    }


    // methods

    /// create Photo
    public VideoResponseDTO createVideo(CreateVideoRequestDTO payload) {
        Account author = this.accountService.findById(payload.authorId());
        Video newVideo = new Video(author, payload.caption(), payload.videoUrl(), payload.durationSeconds());
        Post saved = this.postService.save(newVideo);
        log.info("Post type:'Video' created.");
        return VideoResponseDTO.fromEntity((Video) saved);
    }
}
