package wlsh.project.intervai.interview.infra;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.session.domain.SessionStatus;

public interface InterviewRepository extends JpaRepository<InterviewEntity, Long> {

    Optional<InterviewEntity> findByIdAndStatus(Long id, EntityStatus status);

    @Query(value = """
            SELECT i.id AS id, i.interviewType AS interviewType, i.difficulty AS difficulty,
                   i.questionCount AS questionCount, i.createdAt AS createdAt, s.sessionStatus AS sessionStatus
            FROM InterviewEntity i
            JOIN InterviewSessionEntity s ON s.interviewId = i.id AND s.status = 'ACTIVE'
            WHERE i.userId = :userId
              AND i.status = 'ACTIVE'
              AND (:interviewType IS NULL OR i.interviewType = :interviewType)
              AND (:sessionStatus IS NULL OR s.sessionStatus = :sessionStatus)
            ORDER BY i.createdAt DESC
            """,
            countQuery = """
            SELECT COUNT(i)
            FROM InterviewEntity i
            JOIN InterviewSessionEntity s ON s.interviewId = i.id AND s.status = 'ACTIVE'
            WHERE i.userId = :userId
              AND i.status = 'ACTIVE'
              AND (:interviewType IS NULL OR i.interviewType = :interviewType)
              AND (:sessionStatus IS NULL OR s.sessionStatus = :sessionStatus)
            """)
    Page<InterviewSummaryProjection> findSummaries(
            @Param("userId") Long userId,
            @Param("interviewType") InterviewType interviewType,
            @Param("sessionStatus") SessionStatus sessionStatus,
            Pageable pageable);
}
