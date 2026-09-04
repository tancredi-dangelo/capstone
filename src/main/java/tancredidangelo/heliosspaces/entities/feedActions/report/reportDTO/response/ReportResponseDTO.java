package tancredidangelo.heliosspaces.entities.feedActions.report.reportDTO.response;

import tancredidangelo.heliosspaces.entities.feedActions.report.Report;
import tancredidangelo.heliosspaces.entities.feedActions.report.ReportStatus;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.post.Post;

import java.time.LocalDateTime;

public record ReportResponseDTO(
        Long id,
        Account author,
        Account reportedAccount,
        Post reportedPost,
        String reason,
        LocalDateTime timestamp,
        ReportStatus status
) {
    public static ReportResponseDTO fromEntity(Report report) {
        return new ReportResponseDTO(
                report.getId(),
                report.getAuthor(),
                report.getReportedAccount() != null ? report.getReportedAccount() : null,
                report.getReportedPost() != null ? report.getReportedPost() : null,
                report.getReason(),
                report.getTimestamp(),
                report.getStatus()
        );
    }
}
