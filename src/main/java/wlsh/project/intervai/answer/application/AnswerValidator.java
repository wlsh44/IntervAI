package wlsh.project.intervai.answer.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.answer.infra.AnswerRepository;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.session.application.InterviewSessionValidator;

@Component
@RequiredArgsConstructor
public class AnswerValidator {

    private final AnswerRepository answerRepository;
    private final InterviewSessionValidator interviewSessionValidator;

    public void validate(Long userId, Long interviewId, Long questionId) {
        interviewSessionValidator.validateInterviewOwner(interviewId, userId);
        interviewSessionValidator.validateSessionInProgress(interviewId);
        validateNotAlreadyAnswered(questionId);
    }

    private void validateNotAlreadyAnswered(Long questionId) {
        if (answerRepository.existsByQuestionIdAndStatus(questionId, EntityStatus.ACTIVE)) {
            throw new CustomException(ErrorCode.ANSWER_ALREADY_EXISTS);
        }
    }
}
