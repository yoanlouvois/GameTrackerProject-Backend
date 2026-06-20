package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.enums.ReportStatus;
import com.et4.gametrackerproject.enums.ReportType;
import com.et4.gametrackerproject.model.Report;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReportDto {
    private Integer id;

    private Instant creationDate;

    private Instant lastModifiedDate;

    private UserDto reporter;

    private UserDto reported;

    private ReportType type;

    private Integer contentId;

    private String reason;

    private ReportStatus status;

    private UserDto resolver;

    private Instant resolvedAt;

    public static ReportDto fromEntity(Report report) {
        if(report == null) {
            return null;
            //TODO: throw exception
        }

        return ReportDto.builder()
                .id(report.getId())
                .creationDate(report.getCreationDate())
                .lastModifiedDate(report.getLastModifiedDate())
                .reporter(UserDto.fromEntity(report.getReporter()))
                .reported(UserDto.fromEntity(report.getReported()))
                .type(report.getType())
                .contentId(report.getContentId())
                .reason(report.getReason())
                .status(report.getStatus())
                .resolver(UserDto.fromEntity(report.getResolver()))
                .resolvedAt(report.getResolvedAt())
                .build();
    }

    public static Report toEntity(ReportDto reportDto) {
        if (reportDto == null) {
            return null;
            // TODO: throw exception
        }

        return Report.builder()
                .id(reportDto.getId())
                .creationDate(reportDto.getCreationDate())
                .lastModifiedDate(reportDto.getLastModifiedDate())
                .reporter(UserDto.toEntity(reportDto.getReporter()))
                .reported(UserDto.toEntity(reportDto.getReported()))
                .type(reportDto.getType())
                .contentId(reportDto.getContentId())
                .reason(reportDto.getReason())
                .status(reportDto.getStatus())
                .resolver(UserDto.toEntity(reportDto.getResolver()))
                .resolvedAt(reportDto.getResolvedAt())
                .build();
    }
}
