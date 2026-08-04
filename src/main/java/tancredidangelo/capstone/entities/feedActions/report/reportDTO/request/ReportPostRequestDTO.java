package tancredidangelo.capstone.entities.feedActions.report.reportDTO.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportPostRequestDTO(
        @NotNull Long reportedPostId,
        @NotNull @Size(min = 1, max = 1000) String reason
) {
}
