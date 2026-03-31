package wlsh.project.intervai.question.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    int countBySessionIdAndStatus(Long sessionId, EntityStatus status);
}
