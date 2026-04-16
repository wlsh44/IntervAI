package wlsh.project.intervai.session.infra;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.session.application.dto.SessionHistoryDto;

public interface InterviewSessionRepository extends JpaRepository<InterviewSessionEntity, Long> {

    Optional<InterviewSessionEntity> findByIdAndStatus(Long id, EntityStatus status);

    Optional<InterviewSessionEntity> findByInterviewIdAndStatus(Long interviewId, EntityStatus status);

    List<InterviewSessionEntity> findByInterviewIdInAndStatus(List<Long> interviewIds, EntityStatus status);

    @Query("""
            select q.id as questionId, q.parentQuestionId as parentQuestionId, q.content as questionContent,
                   q.questionType as questionType, q.questionIndex as questionIndex,
                   a.id as answerId, a.content as answerContent
            from QuestionEntity q left join AnswerEntity a on q.id = a.questionId and a.status = :status
            where q.interviewId = :interviewId and q.status = :status
            order by q.questionIndex asc, q.id asc
            """)
    List<SessionHistoryDto> findSessionHistoryByInterviewId(@Param("interviewId") Long interviewId, @Param("status") EntityStatus status);
}
