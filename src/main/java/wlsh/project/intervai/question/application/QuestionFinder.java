package wlsh.project.intervai.question.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.NextQuestionResult;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.question.infra.QuestionRepository;

@Component
@RequiredArgsConstructor
public class QuestionFinder {

    private final QuestionRepository questionRepository;

    public Question find(Long questionId) {
        return questionRepository.findByIdAndStatus(questionId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND))
                .toDomain();
    }

    public NextQuestionResult findCurrent(Long sessionId, int currentMainQuestionIdx,
                                          int followUpCount, int totalQuestionCount) {
        Question question = followUpCount > 0
                ? findLatestFollowUp(sessionId)
                : findMainQuestion(sessionId, currentMainQuestionIdx);

        boolean hasNext = currentMainQuestionIdx < totalQuestionCount - 1;
        return new NextQuestionResult(question, hasNext);
    }

    private Question findLatestFollowUp(Long sessionId) {
        return questionRepository.findFirstBySessionIdAndQuestionTypeAndStatusOrderByIdDesc(
                        sessionId, QuestionType.FOLLOW_UP, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND))
                .toDomain();
    }

    private Question findMainQuestion(Long sessionId, int questionIndex) {
        return questionRepository.findBySessionIdAndQuestionTypeAndQuestionIndexAndStatus(
                        sessionId, QuestionType.QUESTION, questionIndex, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND))
                .toDomain();
    }
}
