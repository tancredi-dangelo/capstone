package tancredidangelo.capstone.entities.post;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.post.postDTO.requests.create.CreateCarouselRequestDTO;
import tancredidangelo.capstone.entities.post.postDTO.requests.create.CreatePhotoRequestDTO;
import tancredidangelo.capstone.entities.post.postDTO.requests.create.CreateVideoRequestDTO;
import tancredidangelo.capstone.entities.post.postDTO.requests.create.CreateWritingRequestDTO;
import tancredidangelo.capstone.entities.post.postDTO.requests.update.UpdatePostRequestDTO;
import tancredidangelo.capstone.entities.post.postDTO.responses.*;
import tancredidangelo.capstone.entities.post.postSubclasses.carousel.CarouselService;
import tancredidangelo.capstone.entities.post.postSubclasses.photo.PhotoService;
import tancredidangelo.capstone.entities.post.postSubclasses.video.VideoService;
import tancredidangelo.capstone.entities.post.postSubclasses.writing.WritingService;
import tancredidangelo.capstone.helpers.ConverterPostDTO;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/posts")
public class PostController {

    /// dependency injection
    private final PostService postService;
    private final WritingService writingService;
    private final PhotoService photoService;
    private final VideoService videoService;
    private final CarouselService carouselService;

    public PostController(PostService postService, WritingService writingService, PhotoService photoService, VideoService videoService, CarouselService carouselService) {
        this.postService = postService;
        this.writingService = writingService;
        this.photoService = photoService;
        this.videoService = videoService;
        this.carouselService = carouselService;
    }


    // -------------------- ENDPOINTS ------------------------------------------------------


    // ----------------------  GET  --------------------------------------------------------------------

    /// GET HOME FEED -> GET "[...](http://localhost/posts/home)" 200 OK
    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    public Page<PostResponseDTO> getFeed(Authentication authentication, Pageable pageable) {
        Long accountId = ((Account) Objects.requireNonNull(authentication.getPrincipal())).getId();
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return this.postService.getFeed(accountId, since, pageable);
    }

    /// GET SINGLE POST -> GET "[...](http://localhost/posts/{id})" 200 OK
    @GetMapping("/{id}")
    public PostResponseDTO getPostById(@PathVariable Long id) {
        Post found = postService.findById(id);
        return ConverterPostDTO.convertToDTO(found);
    }



    // --------------------  CREATE  ------------------------------------------------------------------

    // CREATE POST -> POST "[...](http://localhost/posts)" + {payload} 200 OK
    // Post creation is managed individually with individual endpoints

    @PostMapping("/writings")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public WritingResponseDTO createWriting(@RequestBody @Valid CreateWritingRequestDTO payload) {
        return this.writingService.createWriting(payload);
    }

    @PostMapping("/photos")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public PhotoResponseDTO createPhoto(@RequestBody @Valid CreatePhotoRequestDTO payload) {
        return this.photoService.createPhoto(payload);
    }

    @PostMapping("/carousels")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public CarouselResponseDTO createCarousel(@RequestBody @Valid CreateCarouselRequestDTO payload) {
        return this.carouselService.createCarousel(payload);
    }

    @PostMapping("/videos")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public VideoResponseDTO createVideo(@RequestBody @Valid CreateVideoRequestDTO payload) {
        return this.videoService.createVideo(payload);
    }



    // ------------------  UPDATE --------------------------------------------------------------------------


    /// UPDATE POST -> PUT "[...](http://localhost/posts/{id})" + {payload} 200 OK
    @PutMapping("/{id}")
    @PreAuthorize("@authConfig.isPostOwnerOrAdmin(#id, authentication)")
    public PostResponseDTO updatePostById(@PathVariable Long id, @RequestBody @Valid UpdatePostRequestDTO payload) {
        return this.postService.updateById(id, payload);
    }



    // ------------------- DELETE -----------------------------------------------------------------------------


    /// DELETE POST -> DELETE "[...](http://localhost/posts/{id})" 204 OK
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authConfig.isPostOwnerOrAdmin(#id, authentication)")
    public void deletePostById(@PathVariable Long id) {
        postService.deleteById(id);
    }



}
