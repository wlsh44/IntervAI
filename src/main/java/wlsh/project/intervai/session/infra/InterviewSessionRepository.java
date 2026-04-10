package wlsh.project.intervai.session.infra;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;

public interface InterviewSessionRepository extends JpaRepository<InterviewSessionEntity, Long> {

    Optional<InterviewSessionEntity> findByIdAndStatus(Long id, EntityStatus status);

    Optional<InterviewSessionEntity> findByInterviewIdAndStatus(Long interviewId, EntityStatus status);

    List<InterviewSessionEntity> findByInterviewIdInAndStatus(List<Long> interviewIds, EntityStatus status);
}
