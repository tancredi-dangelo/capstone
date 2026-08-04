package tancredidangelo.capstone.entities.feedActions.report.reportDTO.request;

import jakarta.validation.constraints.NotNull;
import tancredidangelo.capstone.entities.feedActions.report.ReportStatus;

public record UpdateStatusRequestDTO(
        @NotNull ReportStatus newStatus) {
}
