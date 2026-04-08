package wlsh.project.intervai.answer.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.answer.infra.AnswerRepository;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.question.infra.QuestionEntity;
import wlsh.project.intervai.question.infra.QuestionRepository;
import wlsh.project.intervai.session.application.InterviewSessionValidator;

@Component
@RequiredArgsConstructor
public class AnswerValidator {

    private final AnswerRepository answerRepository;
    private final InterviewSessionValidator interviewSessionValidator;
    private final QuestionRepository questionRepository;

    public void validate(Long userId, Long interviewId, Long questionId) {
        interviewSessionValidator.validateInterviewOwner(interviewId, userId);
        interviewSessionValidator.validateSessionInProgress(interviewId);
        validateQuestionInInterview(questionId, interviewId);
        validateNotAlreadyAnswered(questionId);
    }

    private void validateQuestionInInterview(Long questionId, Long interviewId) {
        QuestionEntity question = questionRepository.findByIdAndStatus(questionId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
        if (!question.getInterviewId().equals(interviewId)) {
            throw new CustomException(ErrorCode.QUESTION_NOT_FOUND);
        }
    }

    private void validateNotAlreadyAnswered(Long questionId) {
        if (answerRepository.existsByQuestionIdAndStatus(questionId, EntityStatus.ACTIVE)) {
            throw new CustomException(ErrorCode.ANSWER_ALREADY_EXISTS);
        }
    }
}
