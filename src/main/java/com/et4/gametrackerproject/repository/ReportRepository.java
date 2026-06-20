package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.enums.ReportStatus;
import com.et4.gametrackerproject.enums.ReportType;
import com.et4.gametrackerproject.model.Report;
import com.et4.gametrackerproject.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report,Integer> {

    // Recherches de base par utilisateur
    List<Report> findByReporter(User reporter);

    List<Report> findByReported(User reported);

    List<Report> findByResolver(User resolver);

    Page<Report> findByReporter(User reporter, Pageable pageable);

    Page<Report> findByReported(User reported, Pageable pageable);

    Page<Report> findByResolver(User resolver, Pageable pageable);

    // Recherches par type et statut
    List<Report> findByType(ReportType type);

    List<Report> findByStatus(ReportStatus status);

    List<Report> findByTypeAndStatus(ReportType type, ReportStatus status);

    Page<Report> findByType(ReportType type, Pageable pageable);

    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    Page<Report> findByTypeAndStatus(ReportType type, ReportStatus status, Pageable pageable);

    // Combiner les filtres
    List<Report> findByReportedAndStatus(User reported, ReportStatus status);

    List<Report> findByReportedAndType(User reported, ReportType type);

    List<Report> findByReportedAndTypeAndStatus(User reported, ReportType type, ReportStatus status);

    // Trouver les rapports sur un contenu spécifique
    List<Report> findByTypeAndContentId(ReportType type, Integer contentId);

    Page<Report> findByTypeAndContentId(ReportType type, Integer contentId, Pageable pageable);

    Optional<Report> findByReporterAndReportedAndTypeAndContentId(
            User reporter,
            User reported,
            ReportType type,
            Integer contentId);

    // Vérifier si un utilisateur a déjà été signalé par ce rapporteur
    boolean existsByReporterAndReported(User reporter, User reported);

    boolean existsByContentIdAndType(Integer contentId, ReportType type);

    // Trouver les rapports non résolus
    @Query("SELECT r FROM Report r WHERE r.status = 'PENDING' OR r.status = 'INVESTIGATING'")
    List<Report> findUnresolvedReports();

    @Query("SELECT r FROM Report r WHERE r.status = 'PENDING' OR r.status = 'INVESTIGATING'")
    Page<Report> findUnresolvedReports(Pageable pageable);

    // Compter les rapports par statut
    Long countByStatus(ReportStatus status);

    Long countByTypeAndStatus(ReportType type, ReportStatus status);

    // Mettre à jour le statut d'un rapport
    @Modifying
    @Transactional
    @Query("UPDATE Report r SET r.status = :status, r.resolver = :resolver, r.resolvedAt = CURRENT_TIMESTAMP WHERE r.id = :reportId")
    int updateReportStatus(
            @Param("reportId") Integer reportId,
            @Param("status") ReportStatus status,
            @Param("resolver") User resolver);

    // Trouver les utilisateurs les plus signalés
    @Query("SELECT r.reported.id, r.reported.username, COUNT(r) as reportCount " +
            "FROM Report r " +
            "WHERE r.status <> 'DISMISSED' " +
            "GROUP BY r.reported.id, r.reported.username " +
            "ORDER BY reportCount DESC")
    List<Object[]> findMostReportedUsers(Pageable pageable);

    // Analyser les types de rapports les plus courants
    @Query("SELECT r.type, COUNT(r) as count FROM Report r GROUP BY r.type ORDER BY count DESC")
    List<Object[]> analyzeReportTypeDistribution();

    // Calculer le ratio de résolution par modérateur
    @Query("SELECT r.resolver.id, r.resolver.username, " +
            "COUNT(CASE WHEN r.status = 'RESOLVED' THEN 1 ELSE NULL END) as resolved, " +
            "COUNT(CASE WHEN r.status = 'DISMISSED' THEN 1 ELSE NULL END) as dismissed, " +
            "COUNT(r) as total " +
            "FROM Report r " +
            "WHERE r.resolver IS NOT NULL " +
            "GROUP BY r.resolver.id, r.resolver.username")
    List<Object[]> analyzeResolverEfficiency();

    // Trouver les utilisateurs avec plusieurs rapports sur une période récente
    @Query("SELECT r.reported.id, r.reported.username, COUNT(r) as reportCount " +
            "FROM Report r " +
            "WHERE r.creationDate > :since " +
            "GROUP BY r.reported.id, r.reported.username " +
            "HAVING COUNT(r) >= :minReports " +
            "ORDER BY reportCount DESC")
    List<Object[]> findUsersWithMultipleRecentReports(
            @Param("since") Instant since,
            @Param("minReports") Long minReports);

    // Trouver les rapports d'un utilisateur spécifique
    @Query("SELECT r FROM Report r WHERE r.reporter.id = :userId OR r.reported.id = :userId OR r.resolver.id = :userId")
    Optional<Report> findByUserId(Integer userId);
}
