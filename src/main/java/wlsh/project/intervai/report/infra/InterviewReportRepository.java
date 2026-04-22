package wlsh.project.intervai.report.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;

import java.util.Optional;

public interface InterviewReportRepository extends JpaRepository<InterviewReportEntity, Long> {

    Optional<InterviewReportEntity> findByInterviewIdAndStatus(Long interviewId, EntityStatus status);

    boolean existsByInterviewIdAndStatus(Long interviewId, EntityStatus status);
}
