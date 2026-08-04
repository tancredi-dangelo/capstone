package tancredidangelo.capstone.specifications;

import org.springframework.data.jpa.domain.Specification;
import tancredidangelo.capstone.entities.feedActions.report.Report;
import tancredidangelo.capstone.entities.feedActions.report.ReportStatus;
import tancredidangelo.capstone.exceptions.BadRequestException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReportSpecification {

    public static Specification<Report> hasStatus(ReportStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Report> hasAuthorId(Long authorId) {
        return (root, query, criteriaBuilder) -> {
            if (authorId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("author").get("id"), authorId);
        };
    }

    public static Specification<Report> hasDateInterval(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            }
            if (startDate != null && endDate == null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                return criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDateTime);
            }
            if (startDate == null) {
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                return criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDateTime);
            }

            if (startDate.isAfter(endDate)) {
                throw new BadRequestException("startDate can't be after endDate.");
            }

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            return criteriaBuilder.between(root.get("timestamp"), startDateTime, endDateTime);
        };
    }
}