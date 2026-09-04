package tancredidangelo.heliosspaces.entities.feedActions.report.reportDTO.request;

import jakarta.validation.constraints.NotNull;
import tancredidangelo.heliosspaces.entities.feedActions.report.ReportStatus;

public record UpdateStatusRequestDTO(
        @NotNull ReportStatus newStatus) {
}
