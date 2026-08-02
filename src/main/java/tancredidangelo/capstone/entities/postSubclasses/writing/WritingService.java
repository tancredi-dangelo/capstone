package tancredidangelo.capstone.entities.postSubclasses.writing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.account.stack.AccountService;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.PostService;
import tancredidangelo.capstone.entities.post.postDTO.requests.create.CreatePhotoRequestDTO;
import tancredidangelo.capstone.entities.post.postDTO.requests.create.CreateWritingRequestDTO;
import tancredidangelo.capstone.entities.post.postDTO.responses.PhotoResponseDTO;
import tancredidangelo.capstone.entities.post.postDTO.responses.WritingResponseDTO;
import tancredidangelo.capstone.entities.postSubclasses.photo.Photo;


@Service
@Slf4j
public class WritingService {

    /// dependency injection

    private final PostService postService;
    private final AccountService accountService;

    public WritingService(PostService postService, AccountService accountService) {
        this.postService = postService;
        this.accountService = accountService;
    }


    // methods

    /// create Writing
    public WritingResponseDTO createWriting(CreateWritingRequestDTO payload) {
        Account author = this.accountService.findById(payload.authorId());
        Writing newWriting = new Writing(author, payload.text());
        Post saved = this.postService.save(newWriting);
        log.info("Post type:'Writing' created.");
        return WritingResponseDTO.fromEntity((Writing) saved);
    }
}