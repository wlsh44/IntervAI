package wlsh.project.intervai.answer.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.answer.infra.AnswerEntity;
import wlsh.project.intervai.answer.infra.AnswerRepository;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.feedback.application.FeedbackManager;
import wlsh.project.intervai.question.infra.QuestionEntity;
import wlsh.project.intervai.question.infra.QuestionRepository;

@Component
@RequiredArgsConstructor
public class AnswerManager {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final FeedbackManager feedbackManager;

    @Transactional
    public Answer create(Long userId, Long questionId, String content) {
        QuestionEntity question = questionRepository.findByIdAndStatus(questionId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
        Answer answer = Answer.create(userId, question.getInterviewId(), question.getSessionId(), questionId, content);
        AnswerEntity entity = answerRepository.save(AnswerEntity.from(answer));
        return entity.toDomain();
    }

    @Transactional
    public void deleteByInterviewId(Long interviewId) {
        List<AnswerEntity> answers = answerRepository.findByInterviewIdAndStatus(interviewId, EntityStatus.ACTIVE);
        List<Long> answerIds = answers.stream().map(AnswerEntity::getId).toList();
        feedbackManager.deleteByAnswerIds(answerIds);
        answers.forEach(AnswerEntity::delete);
    }
}
