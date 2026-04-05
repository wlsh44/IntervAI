package wlsh.project.intervai.question.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    Optional<QuestionEntity> findByIdAndStatus(Long id, EntityStatus status);

    List<QuestionEntity> findBySessionIdAndStatusOrderByQuestionIndexAsc(Long sessionId, EntityStatus status);

}
