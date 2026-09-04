package tancredidangelo.heliosspaces.entities.post.postSubclasses.writing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.person.account.stack.AccountService;
import tancredidangelo.heliosspaces.entities.post.Post;
import tancredidangelo.heliosspaces.entities.post.PostService;
import tancredidangelo.heliosspaces.entities.post.postDTO.requests.create.CreateWritingRequestDTO;
import tancredidangelo.heliosspaces.entities.post.postDTO.responses.WritingResponseDTO;


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
    public WritingResponseDTO createWriting(Long authorId, CreateWritingRequestDTO payload) {
        Account author = this.accountService.findById(authorId);
        Writing newWriting = new Writing(author, payload.text());
        Post saved = this.postService.save(newWriting);
        log.info("Post type:'Writing' created.");
        return WritingResponseDTO.fromEntity((Writing) saved);
    }
}