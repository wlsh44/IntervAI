package wlsh.project.intervai.question.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.question.domain.QuestionType;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    Optional<QuestionEntity> findByIdAndStatus(Long id, EntityStatus status);

    List<QuestionEntity> findByInterviewIdAndStatusOrderByQuestionIndexAsc(Long interviewId, EntityStatus status);

    Optional<QuestionEntity> findBySessionIdAndQuestionTypeAndQuestionIndexAndStatus(
            Long sessionId, QuestionType questionType, int questionIndex, EntityStatus status);

    Optional<QuestionEntity> findFirstBySessionIdAndQuestionTypeAndStatusOrderByIdDesc(
            Long sessionId, QuestionType questionType, EntityStatus status);

    List<QuestionEntity> findBySessionIdAndQuestionTypeAndStatusOrderByQuestionIndexAsc(
            Long sessionId, QuestionType questionType, EntityStatus status);

}
