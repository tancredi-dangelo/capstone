package tancredidangelo.capstone.entities.post;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.feedActions.follow.FollowService;
import tancredidangelo.capstone.entities.person.account.accountDTOs.responses.PublicAccountResponseDTO;
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
import tancredidangelo.capstone.exceptions.ForbiddenException;
import tancredidangelo.capstone.exceptions.UnauthorizedException;
import tancredidangelo.capstone.helpers.ConverterPostDTO;


import java.util.List;
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
    private final FollowService followService;

    public PostController(PostService postService, WritingService writingService, PhotoService photoService, VideoService videoService, CarouselService carouselService, FollowService followService) {
        this.postService = postService;
        this.writingService = writingService;
        this.photoService = photoService;
        this.videoService = videoService;
        this.carouselService = carouselService;
        this.followService = followService;
    }

    // -------------------------------- GET --------------------------------

    /// GET HOME FEED -> GET "/posts/home"
    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    public List<PostResponseDTO> getFeed(
            Authentication authentication
    ) {
        Long accountId = ((Account) Objects.requireNonNull(authentication.getPrincipal())).getId();
        return this.postService.getFeed(accountId);
    }

    /// GET OWN POSTS -> GET "/posts/accounts/me"
    @GetMapping("/accounts/me")
    @PreAuthorize("isAuthenticated()")
    public List<PostResponseDTO> getOwnPosts(
            Authentication authentication
    ) {
        Account ownAccount = (Account) authentication.getPrincipal();
        assert ownAccount != null;
        List<Post> posts = this.postService.getPostsByAccountId(ownAccount.getId());

        return posts.stream()
                .map(ConverterPostDTO::convertToDTO)
                .toList();
    }

    /// GET POSTS BY ACCOUNT ID -> GET "/posts/accounts/{id}"
    @GetMapping("/accounts/{id}")
    @PreAuthorize("isAuthenticated()")
    public List<PostResponseDTO> getPostsByAccountId(
            @PathVariable Long id
    ) {
        List<Post> posts = this.postService.getPostsByAccountId(id);

        return posts.stream()
                .map(ConverterPostDTO::convertToDTO)
                .toList();
    }


    /// GET POSTS BY ACCOUNT USERNAME
    @GetMapping("/accounts/username/{username}")
    @PreAuthorize("isAuthenticated()")
    public List<PostResponseDTO> getPostsByUsername(
            @PathVariable String username
    ) {
        List<Post> posts = this.postService.getPostsByUsername(username);

        return posts.stream()
                .map(ConverterPostDTO::convertToDTO)
                .toList();
    }

    /// GET SINGLE POST -> GET "/posts/{id}"
    @GetMapping("/{id:\\d+}")
    @PreAuthorize("isAuthenticated()")
    public PostResponseDTO getPostById(@PathVariable Long id, Authentication authentication) {
        Post found = this.postService.findById(id);
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        Account author = found.getAuthor();

        boolean ifFollower = this.followService.existsByFollowerIdAndFollowedId(authenticatedAccount.getId(), authenticatedAccount.getId());

        if (!author.getId().equals(authenticatedAccount.getId()) && author.isPrivate() && author.getFollowers() != null && ifFollower) {
            if (!authenticatedAccount.getRole().name().equals("ROLE_ADMIN")) {
                throw new ForbiddenException("This account is private. Request follow to see its contents.");
            }
        }

        return ConverterPostDTO.convertToDTO(found);
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
        assert authenticatedAccount != null;
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
        assert authenticatedAccount != null;
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
        assert authenticatedAccount != null;
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
        assert authenticatedAccount != null;
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