package tancredidangelo.capstone.entities.tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.tag.tagDTO.request.CreateTagRequestDTO;
import tancredidangelo.capstone.entities.tag.tagDTO.response.TagResponseDTO;
import tancredidangelo.capstone.entities.tag.tagDTO.request.UpdateTagRequestDTO;

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
    @PreAuthorize("@authConfig.isAdmin()")
    public TagResponseDTO createTag(CreateTagRequestDTO payload) {
        return this.tagService.save(payload);
    }


    // GET TAG BY ID
    @GetMapping("/{id}")
    @PreAuthorize("@authConfig.isAdmin()")
    public Tag findTagById(@PathVariable Long id) {
        return this.tagService.findById(id);
    }

    // GET TAG BY TITLE
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public TagResponseDTO findByTitle(@RequestParam String title) {
        return this.tagService.findByTitle(title);
    }

    // GET TAGS MATCHING TITLE
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<TagResponseDTO> getTagsByTitleMatch(@RequestParam String match) {
        return this.tagService.findByTitleMatching(match);
    }

    // GET ALL TAGS (PAGE)
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<TagResponseDTO> getAllTags(@RequestParam int page,
                                @RequestParam int size,
                                @RequestParam(defaultValue = "name") String sortField,
                                @RequestParam(defaultValue = "asc") String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, direction);
        return this.tagService.findAll(pageable);
    }


    // UPDATE TAG
    @PutMapping("/{id}")
    @PreAuthorize("@authConfig.isAdmin()")
    public TagResponseDTO updateTagById(@PathVariable Long id, @RequestParam UpdateTagRequestDTO payload) {
        return this.tagService.updateById(id, payload);
    }


    // DELETE TAG
    @DeleteMapping("/{id}")
    @PreAuthorize("@authConfig.isAdmin()")
    public void updateTagById(@PathVariable Long id) {
        this.tagService.deleteById(id);
    }


}
