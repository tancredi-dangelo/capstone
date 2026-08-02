package tancredidangelo.capstone.entities.postSubclasses.writing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.PostService;
import tancredidangelo.capstone.entities.post.postDTO.WritingResponseDTO;
import tancredidangelo.capstone.entities.postSubclasses.writing.writingDTO.CreateWritingRequestDTO;
import tancredidangelo.capstone.entities.postSubclasses.writing.writingDTO.UpdateWritingRequestDTO;
import tancredidangelo.capstone.exceptions.BadRequestException;

@Service
@Slf4j
public class WritingService {

    private final PostService postService;

    public WritingService(PostService postService) {
        this.postService = postService;
    }



    // ------------------------------- methods ------------------------------------------

    /// Create new Writing
    public WritingResponseDTO createWriting(CreateWritingRequestDTO payload) {
        Writing newWritingPost = new Writing(payload.author(), payload.text());

        Post saved = this.postService.save(newWritingPost);
        log.info("Writing Post created with ID: {}", saved.getId());

        return WritingResponseDTO.fromEntity((Writing) saved);
    }

    /// Update Writing post
    public WritingResponseDTO updateById(Long id, UpdateWritingRequestDTO payload) {

        Post found = this.postService.findById(id);

        if (!(found instanceof Writing writingToUpdate)) {
            throw new BadRequestException("Post is not type 'Writing'");
        }

        writingToUpdate.setText(payload.text());
        writingToUpdate.setUpdated(true);

        Post saved = this.postService.save(writingToUpdate);
        log.info("Writing Post with ID {} updated.", saved.getId());

        return WritingResponseDTO.fromEntity((Writing) saved);
    }

    /// Delete Writing post
    public void deleteById(Long id) {
        // Delega la cancellazione direttamente al PostService generico per ID
        this.postService.deleteById(id);
        log.info("Writing Post with ID {} deleted.", id);
    }
}