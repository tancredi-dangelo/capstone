package tancredidangelo.heliosspaces.entities.post.postSubclasses.carousel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tancredidangelo.heliosspaces.cloudinary.CloudinaryService;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.person.account.stack.AccountService;
import tancredidangelo.heliosspaces.entities.post.Post;
import tancredidangelo.heliosspaces.entities.post.PostService;
import tancredidangelo.heliosspaces.entities.post.postDTO.requests.create.CreateCarouselRequestDTO;
import tancredidangelo.heliosspaces.entities.post.postDTO.responses.CarouselResponseDTO;

import java.util.List;

@Service
@Slf4j
public class CarouselService {

    private final PostService postService;
    private final AccountService accountService;
    private final CloudinaryService cloudinaryService;

    public CarouselService(PostService postService, AccountService accountService, CloudinaryService cloudinaryService) {
        this.postService = postService;
        this.accountService = accountService;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    public CarouselResponseDTO createCarousel(Long authorId, CreateCarouselRequestDTO payload) {

        Account author = this.accountService.findById(authorId);

        List<String> mediaUrls = payload.files().stream()
                .map(file -> this.cloudinaryService.uploadMedia(file, "posts/carousels"))
                .toList();

        Carousel newCarousel = new Carousel(author, payload.caption(), mediaUrls);
        Post saved = this.postService.save(newCarousel);

        log.info("Post type:'Carousel' created with ID: {} and {} media files.", saved.getId(), mediaUrls.size());

        return CarouselResponseDTO.fromEntity((Carousel) saved);
    }
}