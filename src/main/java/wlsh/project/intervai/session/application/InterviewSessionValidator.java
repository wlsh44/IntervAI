package wlsh.project.intervai.session.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.application.InterviewFinder;
import wlsh.project.intervai.interview.infra.InterviewEntity;

@Component
@RequiredArgsConstructor
public class InterviewSessionValidator {

    private final InterviewFinder interviewFinder;

    public void validateInterviewOwner(Long interviewId, Long userId) {
        InterviewEntity interviewEntity = interviewFinder.getEntity(interviewId);
        if (!interviewEntity.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.INTERVIEW_ACCESS_DENIED);
        }
    }
}
