package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.ReportDto;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.enums.ReportStatus;
import com.et4.gametrackerproject.enums.ReportType;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidEntityException;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.Report;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.repository.ReportRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.ReportService;
import com.et4.gametrackerproject.validator.ReportValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReportDto createReport(ReportDto reportDto) {
        List<String> errors = ReportValidator.validate(reportDto);
        if (!errors.isEmpty()) {
            log.error("Report is not valid: {}", errors);
            throw new InvalidEntityException("Report is not valid", ErrorCodes.REPORT_NOT_VALID, errors);
        }

        log.info("Report created");

        return ReportDto.fromEntity(
                reportRepository.save(
                        ReportDto.toEntity(reportDto)
                )
        );
    }

    @Override
    public ReportDto updateReportDetails(Integer reportId, ReportDto reportDto) {
        if(reportId == null){
            log.error("Report ID is null");
            throw new InvalidEntityException("Report ID is null", ErrorCodes.REPORT_NOT_VALID);
        }
        List<String> errors = ReportValidator.validate(reportDto);
        if (!errors.isEmpty()) {
            log.error("Report is not valid: {}", errors);
            throw new InvalidEntityException("Report is not valid", ErrorCodes.REPORT_NOT_VALID, errors);
        }
        if(reportRepository.findById(reportId).isEmpty()){
            log.error("Report with ID {} not found", reportId);
            throw new EntityNotFoundException("Report not found", ErrorCodes.REPORT_NOT_FOUND);
        }

        log.info("Report updated");

        return ReportDto.fromEntity(
                reportRepository.save(
                        ReportDto.toEntity(reportDto)
                )
        );
    }

    @Override
    public void deleteReport(Integer reportId) {
        if(reportId == null){
            log.error("Report ID is null");
            throw new InvalidEntityException("Report ID is null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(reportRepository.findById(reportId).isEmpty()){
            log.error("Report with ID {} not found", reportId);
            throw new EntityNotFoundException("Report not found", ErrorCodes.REPORT_NOT_FOUND);
        }

        log.info("Report deleted");

        Optional<User> users = userRepository.findByReportId(reportId);
        if (users.isPresent()) {
            log.error("Impossible de supprimer le report avec l'ID {} car il est référencé par un user", reportId);
            throw new InvalidOperationException("Impossible de supprimer le report car il est référencé par un user",
                    ErrorCodes.REPORT_ALREADY_USED);
        }

        reportRepository.deleteById(reportId);
    }

    @Override
    public ReportDto resolveReport(Integer reportId, Integer adminId, String resolutionNotes) {
        if(reportId == null){
            log.error("Report ID is null");
            throw new InvalidEntityException("Report ID is null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(adminId == null){
            log.error("Admin ID is null");
            throw new InvalidEntityException("Admin ID is null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(resolutionNotes == null){
            log.error("Resolution notes are null");
            throw new InvalidEntityException("Resolution notes are null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(reportRepository.findById(reportId).isEmpty()){
            log.error("Report with ID {} not found", reportId);
            throw new EntityNotFoundException("Report not found", ErrorCodes.REPORT_NOT_FOUND);
        }
        if(userRepository.findById(adminId).isEmpty()){
            log.error("Admin with ID {} not found", adminId);
            throw new EntityNotFoundException("Admin not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Report {} resolved", reportId);

        ReportDto reportDto = ReportDto.fromEntity(reportRepository.findById(reportId).orElseThrow());

        User admin = userRepository.findById(adminId).orElse(null);

        reportDto.setStatus(ReportStatus.RESOLVED);
        reportDto.setResolver(UserDto.fromEntity(admin));
        reportDto.setResolvedAt(Instant.now());
        reportDto.setReason(resolutionNotes);

        return ReportDto.fromEntity(
                reportRepository.save(
                        ReportDto.toEntity(reportDto)
                )
        );
    }

    @Override
    public ReportDto changeReportStatus(Integer reportId, ReportStatus newStatus) {
        if(reportId == null){
            log.error("Report ID is null");
            throw new InvalidEntityException("Report ID is null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(newStatus == null){
            log.error("New status is null");
            throw new InvalidEntityException("New status is null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(reportRepository.findById(reportId).isEmpty()){
            log.error("Report with ID {} not found", reportId);
            throw new EntityNotFoundException("Report not found", ErrorCodes.REPORT_NOT_FOUND);
        }

        log.info("Report status changed to {}", newStatus);

        ReportDto reportDto = ReportDto.fromEntity(reportRepository.findById(reportId).orElseThrow());

        reportDto.setStatus(newStatus);

        return ReportDto.fromEntity(
                reportRepository.save(
                        ReportDto.toEntity(reportDto)
                )
        );
    }

    @Override
    public ReportDto getReportById(Integer reportId) {
        if(reportId == null){
            log.error("Report ID is null");
            throw new InvalidEntityException("Report ID is null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(reportRepository.findById(reportId).isEmpty()){
            log.error("Report with ID {} not found", reportId);
            throw new EntityNotFoundException("Report not found", ErrorCodes.REPORT_NOT_FOUND);
        }

        log.info("Report found by id {}", reportId);

        return ReportDto.fromEntity(reportRepository.findById(reportId).orElseThrow());
    }

    @Override
    public Page<ReportDto> getReportsByStatus(ReportStatus status, Pageable pageable) {
        if(status == null){
            log.error("Status is null");
            throw new InvalidEntityException("Status is null", ErrorCodes.REPORT_NOT_VALID);
        }

        log.info("Reports found by status {}", status);

        return reportRepository.findByStatus(status, pageable).map(ReportDto::fromEntity);
    }

    @Override
    public Page<ReportDto> getReportsByType(ReportType type, Pageable pageable) {
        if(type == null){
            log.error("Type is null");
            throw new InvalidEntityException("Type is null", ErrorCodes.REPORT_NOT_VALID);
        }

        log.info("Reports found by type {}", type);

        return reportRepository.findByType(type, pageable).map(ReportDto::fromEntity);
    }

    @Override
    public Page<ReportDto> getUserReports(Integer userId, boolean isReporter, Pageable pageable) {
        if(userId == null){
            log.error("User ID is null");
            throw new InvalidEntityException("User ID is null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(userRepository.findById(userId).isEmpty()){
            log.error("User with ID {} not found", userId);
            throw new EntityNotFoundException("User not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Reports found for user {}", userId);

        if(isReporter){
            return reportRepository.findByReporter(userRepository.findById(userId).orElseThrow(), pageable).map(ReportDto::fromEntity);
        } else {
            return reportRepository.findByReported(userRepository.findById(userId).orElseThrow(), pageable).map(ReportDto::fromEntity);
        }
    }

    @Override
    public Page<ReportDto> getUnresolvedReports(Pageable pageable) {
        log.info("Unresolved reports found");

        return reportRepository.findUnresolvedReports(pageable).map(ReportDto::fromEntity);
    }

    @Override
    public ReportDto assignReportToAdmin(Integer reportId, Integer adminId) {
        if(reportId == null){
            log.error("Report ID is null");
            throw new InvalidEntityException("Report ID is null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(adminId == null){
            log.error("Admin ID is null");
            throw new InvalidEntityException("Admin ID is null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(reportRepository.findById(reportId).isEmpty()){
            log.error("Report with ID {} not found", reportId);
            throw new EntityNotFoundException("Report not found", ErrorCodes.REPORT_NOT_FOUND);
        }
        if(userRepository.findById(adminId).isEmpty()){
            log.error("Admin with ID {} not found", adminId);
            throw new EntityNotFoundException("Admin not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Report {} assigned to admin {}", reportId, adminId);

        ReportDto reportDto = ReportDto.fromEntity(reportRepository.findById(reportId).orElseThrow());

        User admin = userRepository.findById(adminId).orElse(null);

        reportDto.setResolver(UserDto.fromEntity(admin));

        return ReportDto.fromEntity(
                reportRepository.save(
                        ReportDto.toEntity(reportDto)
                )
        );
    }

    @Override
    public boolean hasPreviousReportsAgainstUser(Integer reporterId, Integer reportedUserId) {
        if(reporterId == null){
            log.error("Reporter ID is null");
            throw new InvalidEntityException("Reporter ID is null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(reportedUserId == null){
            log.error("Reported user ID is null");
            throw new InvalidEntityException("Reported user ID is null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(userRepository.findById(reporterId).isEmpty()){
            log.error("Reporter with ID {} not found", reporterId);
            throw new EntityNotFoundException("Reporter not found", ErrorCodes.USER_NOT_FOUND);
        }
        if(userRepository.findById(reportedUserId).isEmpty()){
            log.error("Reported user with ID {} not found", reportedUserId);
            throw new EntityNotFoundException("Reported user not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Previous reports found against user {} ?", reportedUserId);

        return reportRepository.existsByReporterAndReported(
                userRepository.findById(reporterId).orElseThrow(),
                userRepository.findById(reportedUserId).orElseThrow()
        );
    }

    @Override
    public boolean isContentAlreadyReported(Integer contentId, ReportType type) {
        if(contentId == null){
            log.error("Content ID is null");
            throw new InvalidEntityException("Content ID is null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(type == null){
            log.error("Type is null");
            throw new InvalidEntityException("Type is null", ErrorCodes.REPORT_NOT_VALID);
        }

        log.info("Content {} already reported ?", contentId);

        return reportRepository.existsByContentIdAndType(contentId, type);
    }

    @Override
    public Page<ReportDto> getReportHistory(Integer contentId, ReportType type, Pageable pageable) {
        if(contentId == null){
            log.error("Content ID is null");
            throw new InvalidEntityException("Content ID is null", ErrorCodes.REPORT_NOT_VALID);
        }
        if(type == null){
            log.error("Type is null");
            throw new InvalidEntityException("Type is null", ErrorCodes.REPORT_NOT_VALID);
        }

        log.info("Report history found for content {}", contentId);

        return reportRepository.findByTypeAndContentId(type, contentId, pageable).map(ReportDto::fromEntity);
    }
}
