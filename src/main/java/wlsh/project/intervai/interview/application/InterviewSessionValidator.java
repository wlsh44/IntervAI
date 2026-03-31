package wlsh.project.intervai.interview.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.CreateInterviewSessionCommand;
import wlsh.project.intervai.interview.domain.InterviewType;

@Component
@RequiredArgsConstructor
public class InterviewSessionValidator {

    private static final int MINIMUM_QUESTION = 5;
    private static final int MAXIMUM_QUESTION = 10;

    public void validate(CreateInterviewSessionCommand command) {
        validateQuestionCount(command.questionCount());
        validateCsSubjects(command);
        validatePortfolioLinks(command);
    }

    private void validateQuestionCount(int questionCount) {
        if (questionCount < MINIMUM_QUESTION || questionCount > MAXIMUM_QUESTION) {
            throw new CustomException(ErrorCode.INVALID_QUESTION_COUNT);
        }
    }

    private void validateCsSubjects(CreateInterviewSessionCommand command) {
        InterviewType type = command.interviewType();
        if (type == InterviewType.CS || type == InterviewType.ALL) {
            if (command.csSubjects() == null || command.csSubjects().isEmpty()) {
                throw new CustomException(ErrorCode.CS_SUBJECT_REQUIRED);
            }
        }
    }

    private void validatePortfolioLinks(CreateInterviewSessionCommand command) {
        InterviewType type = command.interviewType();
        if (type == InterviewType.PORTFOLIO || type == InterviewType.ALL) {
            if (command.portfolioLinks() == null || command.portfolioLinks().isEmpty()) {
                throw new CustomException(ErrorCode.PORTFOLIO_LINK_REQUIRED);
            }
        }
    }
}
