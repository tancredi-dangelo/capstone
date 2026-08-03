package tancredidangelo.capstone.entities.post.postSubclasses.photo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.account.stack.AccountService;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.PostService;
import tancredidangelo.capstone.entities.post.postDTO.requests.create.CreatePhotoRequestDTO;
import tancredidangelo.capstone.entities.post.postDTO.responses.PhotoResponseDTO;

@Service
@Slf4j
public class PhotoService {

    /// dependency injection

    private final PostService postService;
    private final AccountService accountService;

    public PhotoService(PostService postService, AccountService accountService) {
        this.postService = postService;
        this.accountService = accountService;
    }


    // methods

    /// create Photo
    public PhotoResponseDTO createPhoto(CreatePhotoRequestDTO payload) {
        Account author = this.accountService.findById(payload.authorId());
        Photo newPhoto = new Photo(author, payload.caption(), payload.photoUrl());
        Post saved = this.postService.save(newPhoto);
        log.info("Post type:'Photo' created.");
        return PhotoResponseDTO.fromEntity((Photo) saved);
    }
}
