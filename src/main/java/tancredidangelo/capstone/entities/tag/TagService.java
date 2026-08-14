package tancredidangelo.capstone.entities.tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.tag.tagDTO.request.TagRequestDTO;
import tancredidangelo.capstone.entities.tag.tagDTO.response.TagResponseDTO;
import tancredidangelo.capstone.exceptions.NotFoundException;

import java.util.List;

@Service
@Slf4j
public class TagService {

    /// dependency injection
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }


    /// methods

    public TagResponseDTO save(TagRequestDTO payload) {
        Tag newTag = new Tag(payload.title());
        Tag saved = this.tagRepository.save(newTag);
        return TagResponseDTO.fromEntity(saved);
    }

    public boolean existsByTitle(String title) {
        return this.tagRepository.existsByTitle(title);
    }

    public Tag findById(Long id) {
        return this.tagRepository.findById(id).orElseThrow(() -> new NotFoundException("Tag not found"));
    }

    public Page<TagResponseDTO> findAll(Pageable pageable) {
        Page<Tag> rawTagsPage = this.tagRepository.findAll(pageable);
        return rawTagsPage.map(TagResponseDTO::fromEntity);
    }

    public TagResponseDTO findByTitle(String title) {
        Tag found = this.tagRepository.findByTitle(title).orElseThrow(() -> new NotFoundException("Tag not found"));
        return TagResponseDTO.fromEntity(found);
    }

    public List<TagResponseDTO> findByTitleMatching(String match) {
        List<Tag> rawTagsPage = this.tagRepository.findByTitleContainingIgnoreCase(match);
        return rawTagsPage.stream().map(TagResponseDTO::fromEntity).toList();
    }

    public TagResponseDTO updateById(Long id, TagRequestDTO payload) {
        Tag found = findById(id);
        found.setTitle(payload.title());
        Tag saved = this.tagRepository.save(found);
        return TagResponseDTO.fromEntity(saved);
    }

    public void deleteById(Long id) {
        Tag found = findById(id);
        this.tagRepository.delete(found);
    }
}