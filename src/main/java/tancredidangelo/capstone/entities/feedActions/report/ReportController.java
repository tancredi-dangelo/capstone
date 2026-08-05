package tancredidangelo.capstone.entities.feedActions.report;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.feedActions.report.reportDTO.request.ReportAccountRequestDTO;
import tancredidangelo.capstone.entities.feedActions.report.reportDTO.request.ReportPostRequestDTO;
import tancredidangelo.capstone.entities.feedActions.report.reportDTO.request.UpdateStatusRequestDTO;
import tancredidangelo.capstone.entities.feedActions.report.reportDTO.response.ReportResponseDTO;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // --------------------  ENDPOINTS  ---------------------------------------------------------------------

    /// CREATE ACCOUNT REPORT
    @PostMapping("/account/{accountId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ReportResponseDTO createAccountReport(@PathVariable Long accountId, @RequestBody @Valid ReportAccountRequestDTO payload, Authentication authentication) {
        return this.reportService.createAccountReport(accountId, payload, authentication);
    }

    /// CREATE POST REPORT
    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ReportResponseDTO createPostReport(@PathVariable Long postId, @RequestBody @Valid ReportPostRequestDTO payload, Authentication authentication) {
        return this.reportService.createPostReport(postId, payload, authentication);
    }

    /// ADMIN -> GET REPORTS LIST SORTED AND FILTERED
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<ReportResponseDTO> getReports(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return this.reportService.findReportsFiltered(status, authorId, startDate, endDate, pageable);
    }

    /// ADMIN -> UPDATE STATUS BY ID
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ReportResponseDTO updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateStatusRequestDTO payload
    ) {
        return this.reportService.updateReportStatus(id, payload);
    }
}