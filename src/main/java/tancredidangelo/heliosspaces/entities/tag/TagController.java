package tancredidangelo.heliosspaces.entities.tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import tancredidangelo.heliosspaces.entities.tag.tagDTO.request.TagRequestDTO;
import tancredidangelo.heliosspaces.entities.tag.tagDTO.response.TagResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    /// dependency injection
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    /// methods

    // CREATE TAG
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public TagResponseDTO createTag(@RequestBody TagRequestDTO payload) {
        return this.tagService.save(payload);
    }


    // GET TAG BY ID
    @GetMapping("/{id}")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public TagResponseDTO findTagById(@PathVariable Long id) {
        return TagResponseDTO.fromEntity(this.tagService.findById(id));
    }

    // GET TAG BY TITLE
    @GetMapping("/title/{title}")
    @PreAuthorize("isAuthenticated()")
    public TagResponseDTO findByTitle(@PathVariable String title) {
        return this.tagService.findByTitle(title);
    }

    // GET TAGS MATCHING TITLE
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public List<TagResponseDTO> getTagsByTitleMatch(@RequestParam String title) {
        return this.tagService.findByTitleMatching(title);
    }

    // GET ALL TAGS (PAGE)
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<TagResponseDTO> getAllTags(@RequestParam int page,
                                           @RequestParam int size,
                                           @RequestParam(defaultValue = "title") String sortField,
                                           @RequestParam(defaultValue = "asc") String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, direction, sortField);
        return this.tagService.findAll(pageable);
    }


    // UPDATE TAG
    @PutMapping("/{id}")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public TagResponseDTO updateTagById(@PathVariable Long id, @RequestBody TagRequestDTO payload) {
        return this.tagService.updateById(id, payload);
    }


    // UPDATE OWN ACCOUNT TAGS
    // *** Logic belonging to Account Controller ***

    // DELETE TAG
    @DeleteMapping("/{id}")
    @PreAuthorize("@authConfig.isAdmin(authentication)")
    public void deleteTagById(@PathVariable Long id) {
        this.tagService.deleteById(id);
    }


}