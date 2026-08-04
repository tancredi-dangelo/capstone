package tancredidangelo.capstone.entities.feedActions.report;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.feedActions.report.reportDTO.request.ReportAccountRequestDTO;
import tancredidangelo.capstone.entities.feedActions.report.reportDTO.request.ReportPostRequestDTO;
import tancredidangelo.capstone.entities.feedActions.report.reportDTO.request.UpdateStatusRequestDTO;
import tancredidangelo.capstone.entities.feedActions.report.reportDTO.response.ReportResponseDTO;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.account.stack.AccountService;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.PostService;
import tancredidangelo.capstone.exceptions.BadRequestException;
import tancredidangelo.capstone.exceptions.NotFoundException;
import tancredidangelo.capstone.specifications.ReportSpecification;

import java.time.LocalDate;

@Service
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final AccountService accountService;
    private final PostService postService;

    public ReportService(ReportRepository reportRepository, AccountService accountService, PostService postService) {
        this.reportRepository = reportRepository;
        this.accountService = accountService;
        this.postService = postService;
    }

    // ----------------------------  METHODS  ------------------------------------------------------------------

    /// CREATE ACCOUNT REPORT
    @Transactional
    public ReportResponseDTO createAccountReport(ReportAccountRequestDTO payload, Authentication authentication) {
        Account author = (Account) authentication.getPrincipal();

        if (author.getId().equals(payload.reportedAccountId())) {
            throw new BadRequestException("You cannot report yourself.");
        }

        Account reportedAccount = this.accountService.findById(payload.reportedAccountId());
        Report report = new Report(author, reportedAccount, payload.reason());

        log.info("Report created by Account ID {} against Account ID {}.", author.getId(), reportedAccount.getId());
        Report saved = this.reportRepository.save(report);

        return ReportResponseDTO.fromEntity(saved);
    }

    /// CREATE POST REPORT
    @Transactional
    public ReportResponseDTO createPostReport(ReportPostRequestDTO payload, Authentication authentication) {
        Account author = (Account) authentication.getPrincipal();
        Post reportedPost = this.postService.findById(payload.reportedPostId());

        if (author.getId().equals(reportedPost.getAuthor().getId())) {
            throw new BadRequestException("You cannot report your own post.");
        }

        Report report = new Report(author, reportedPost, payload.reason());

        log.info("Report created by Account ID {} concerning Post ID {}.", author.getId(), reportedPost.getId());
        Report saved = this.reportRepository.save(report);

        return ReportResponseDTO.fromEntity(saved);
    }

    /// FIND BY ID
    public Report findById(Long id) {
        return this.reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Report with ID " + id + " not found."));
    }

    /// GET REPORTS FILTERED (WITH SPECIFICATION)
    public Page<ReportResponseDTO> findReportsFiltered(
            ReportStatus status,
            Long authorId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Specification<Report> spec = Specification
                .where(ReportSpecification.hasStatus(status))
                .and(ReportSpecification.hasAuthorId(authorId))
                .and(ReportSpecification.hasDateInterval(startDate, endDate));

        return this.reportRepository.findAll(spec, pageable)
                .map(ReportResponseDTO::fromEntity);
    }

    /// UPDATE STATUS
    @Transactional
    public ReportResponseDTO updateReportStatus(Long id, UpdateStatusRequestDTO payload) {
        Report report = findById(id);
        report.setStatus(payload.newStatus());

        log.info("Report ID {} status updated to {}.", id, payload.newStatus());

        Report updated = this.reportRepository.save(report);
        return ReportResponseDTO.fromEntity(updated);
    }
}