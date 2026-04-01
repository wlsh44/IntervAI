package wlsh.project.intervai.answer.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {

    boolean existsByQuestionIdAndStatus(Long questionId, EntityStatus status);
}
