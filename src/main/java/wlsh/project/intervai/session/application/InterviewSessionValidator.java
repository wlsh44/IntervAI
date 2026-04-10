package wlsh.project.intervai.session.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.application.InterviewFinder;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.session.infra.InterviewSessionEntity;

@Component
@RequiredArgsConstructor
public class InterviewSessionValidator {

    private final InterviewFinder interviewFinder;
    private final InterviewSessionFinder interviewSessionFinder;

    public void validateInterviewOwner(Long interviewId, Long userId) {
        Interview interview = interviewFinder.find(interviewId);
        if (!interview.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.INTERVIEW_ACCESS_DENIED);
        }
    }

    public void validateSessionInProgress(Long interviewId) {
        InterviewSessionEntity entity = interviewSessionFinder.getEntityByInterviewId(interviewId);
        if (!entity.isInProgress()) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_COMPLETED);
        }
    }
}
