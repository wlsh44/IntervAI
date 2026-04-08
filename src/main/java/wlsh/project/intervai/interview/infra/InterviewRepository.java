package wlsh.project.intervai.interview.infra;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;

public interface InterviewRepository extends JpaRepository<InterviewEntity, Long> {

    Optional<InterviewEntity> findByIdAndStatus(Long id, EntityStatus status);
}
