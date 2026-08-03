package tancredidangelo.capstone.entities.post.postSubclasses.carousel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.account.stack.AccountService;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.PostService;
import tancredidangelo.capstone.entities.post.postDTO.requests.create.CreateCarouselRequestDTO;
import tancredidangelo.capstone.entities.post.postDTO.responses.CarouselResponseDTO;

@Service
@Slf4j
public class CarouselService {

    /// dependency injection
    private final PostService postService;
    private final AccountService accountService;

    public CarouselService(PostService postService, AccountService accountService) {
        this.postService = postService;
        this.accountService = accountService;
    }


    // methods

    /// create carousel
    public CarouselResponseDTO createCarousel(CreateCarouselRequestDTO payload) {
        Account author = this.accountService.findById(payload.authorId());
        Carousel newCarousel = new Carousel(author, payload.caption(), payload.mediaUrls());
        Post saved = this.postService.save(newCarousel);
        log.info("Post type:'Carousel' created.");
        return CarouselResponseDTO.fromEntity((Carousel) saved);
    }
}
