package tancredidangelo.heliosspaces.entities.post.postSubclasses.photo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tancredidangelo.heliosspaces.cloudinary.CloudinaryService;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.person.account.stack.AccountService;
import tancredidangelo.heliosspaces.entities.post.Post;
import tancredidangelo.heliosspaces.entities.post.PostService;
import tancredidangelo.heliosspaces.entities.post.postDTO.requests.create.CreatePhotoRequestDTO;
import tancredidangelo.heliosspaces.entities.post.postDTO.responses.PhotoResponseDTO;


@Service
@Slf4j
public class PhotoService {

    private final PostService postService;
    private final AccountService accountService;
    private final CloudinaryService cloudinaryService;

    public PhotoService(PostService postService, AccountService accountService, CloudinaryService cloudinaryService) {
        this.postService = postService;
        this.accountService = accountService;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    public PhotoResponseDTO createPhoto(Long authorId, CreatePhotoRequestDTO payload) {

        Account author = this.accountService.findById(authorId);

        String photoUrl = this.cloudinaryService.uploadMedia(payload.file(), "posts/photos");

        Photo newPhoto = new Photo(author, payload.caption(), photoUrl);
        Post saved = this.postService.save(newPhoto);

        log.info("Post type:'Photo' created with ID: {}", saved.getId());
        return PhotoResponseDTO.fromEntity((Photo) saved);
    }
}