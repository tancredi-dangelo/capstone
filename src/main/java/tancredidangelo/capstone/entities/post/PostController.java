package tancredidangelo.capstone.entities.post;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    // -------------------------------- GET --------------------------------

    /// GET HOME FEED -> GET "/posts/home"
    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    public Page<PostResponseDTO> getFeed(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long accountId = ((Account) Objects.requireNonNull(authentication.getPrincipal())).getId();
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        Pageable pageable = PageRequest.of(page, size);

        return this.postService.getFeed(accountId, since, pageable);
    }



    /// GET SINGLE POST -> GET "/posts/{id}"
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public PostResponseDTO getPostById(@PathVariable Long id) {
        Post found = postService.findById(id);
        return ConverterPostDTO.convertToDTO(found);
    }



    /// GET POSTS BY ACCOUNT ID -> GET "/posts/accounts/{id}"
    @GetMapping("/accounts/{id}")
    @PreAuthorize("isAuthenticated()")
    public Page<Post> getPostsByAccountId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return this.postService.getPostsByAccountId(id, pageable);
    }



    // -------------------------------- CREATE --------------------------------


    /// CREATE WRITING -> POST "/posts/writings"
    @PostMapping("/writings")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public WritingResponseDTO createWriting(
            @RequestBody @Valid CreateWritingRequestDTO payload,
            Authentication authentication
    ) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return this.writingService.createWriting(authenticatedAccount.getId(), payload);
    }



    /// CREATE PHOTO -> POST "/posts/photos" (Multipart/Form-Data)
    @PostMapping(value = "/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public PhotoResponseDTO createPhoto(
            @Valid @ModelAttribute CreatePhotoRequestDTO payload,
            Authentication authentication
    ) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return this.photoService.createPhoto(authenticatedAccount.getId(), payload);
    }



    /// CREATE CAROUSEL -> POST "/posts/carousels" (Multipart/Form-Data)
    @PostMapping(value = "/carousels", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public CarouselResponseDTO createCarousel(
            @Valid @ModelAttribute CreateCarouselRequestDTO payload,
            Authentication authentication
    ) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return this.carouselService.createCarousel(authenticatedAccount.getId(), payload);
    }



    /// CREATE VIDEO -> POST "/posts/videos" (Multipart/Form-Data)
    @PostMapping(value = "/videos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public VideoResponseDTO createVideo(
            @Valid @ModelAttribute CreateVideoRequestDTO payload,
            Authentication authentication
    ) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return this.videoService.createVideo(authenticatedAccount.getId(), payload);
    }



    // -------------------------------- UPDATE --------------------------------

    /// UPDATE POST -> PUT "/posts/{id}"
    @PutMapping("/{id}")
    @PreAuthorize("@authConfig.isPostOwnerOrAdmin(#id, authentication)")
    public PostResponseDTO updatePostById(
            @PathVariable Long id,
            @RequestBody @Valid UpdatePostRequestDTO payload
    ) {
        return this.postService.updateById(id, payload);
    }



    // -------------------------------- DELETE --------------------------------

    /// DELETE POST -> DELETE "/posts/{id}"
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authConfig.isPostOwnerOrAdmin(#id, authentication)")
    public void deletePostById(@PathVariable Long id) {
        postService.deleteById(id);
    }
}