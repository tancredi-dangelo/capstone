package tancredidangelo.capstone.entities.post;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PostMapping("/home")
public class PostController {

    /// dependency injection
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }



    /// -------------------- ENDPOINTS ------------------------------------------------------

    /// FEED -> GET "http://localhost/home




    /// USER ACCOUNT

}
