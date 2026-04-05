package wlsh.project.intervai.answer.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {

    boolean existsByQuestionIdAndStatus(Long questionId, EntityStatus status);

    Optional<AnswerEntity> findByIdAndStatus(Long id, EntityStatus status);
}
