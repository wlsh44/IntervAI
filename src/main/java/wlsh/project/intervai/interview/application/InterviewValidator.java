package wlsh.project.intervai.interview.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;

@Component
@RequiredArgsConstructor
public class InterviewValidator {

    private static final int MINIMUM_QUESTION = 2;
    private static final int MAXIMUM_QUESTION = 10;

    private final InterviewFinder interviewFinder;

    public void validate(CreateInterviewCommand command) {
        validateQuestionCount(command.questionCount());
        validateCsSubjects(command);
        validatePortfolioLinks(command);
    }

    public void validateOwner(Long interviewId, Long userId) {
        Interview interview = interviewFinder.find(interviewId);
        if (!interview.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.INTERVIEW_ACCESS_DENIED);
        }
    }

    private void validateQuestionCount(int questionCount) {
        if (questionCount < MINIMUM_QUESTION || questionCount > MAXIMUM_QUESTION) {
            throw new CustomException(ErrorCode.INVALID_QUESTION_COUNT);
        }
    }

    private void validateCsSubjects(CreateInterviewCommand command) {
        InterviewType type = command.interviewType();
        if (type == InterviewType.CS || type == InterviewType.ALL) {
            if (command.csSubjects() == null || command.csSubjects().isEmpty()) {
                throw new CustomException(ErrorCode.CS_SUBJECT_REQUIRED);
            }
        }
    }

    private void validatePortfolioLinks(CreateInterviewCommand command) {
        InterviewType type = command.interviewType();
        if (type == InterviewType.PORTFOLIO || type == InterviewType.ALL) {
            if (command.portfolioLinks() == null || command.portfolioLinks().isEmpty()) {
                throw new CustomException(ErrorCode.PORTFOLIO_LINK_REQUIRED);
            }
        }
    }
}
