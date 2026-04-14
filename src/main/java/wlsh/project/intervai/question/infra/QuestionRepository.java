package wlsh.project.intervai.question.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.session.application.dto.SessionHistoryDto;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    Optional<QuestionEntity> findByIdAndStatus(Long id, EntityStatus status);

    Optional<QuestionEntity> findBySessionIdAndQuestionTypeAndQuestionIndexAndStatus(
            Long sessionId, QuestionType questionType, int questionIndex, EntityStatus status);

    Optional<QuestionEntity> findFirstBySessionIdAndQuestionTypeAndStatusOrderByIdDesc(
            Long sessionId, QuestionType questionType, EntityStatus status);

    @Query("""
            select q.id as questionId, q.content as questionContent,
                   q.questionType as questionType, q.questionIndex as questionIndex,
                   a.id as answerId, a.content as answerContent
            from QuestionEntity q left join AnswerEntity a on q.id = a.questionId and a.status = :status
            where q.interviewId = :interviewId and q.status = :status
            order by q.id desc
            """)
    List<SessionHistoryDto> findSessionHistoryByInterviewId(@Param("interviewId") Long interviewId, @Param("status") EntityStatus status);
}
