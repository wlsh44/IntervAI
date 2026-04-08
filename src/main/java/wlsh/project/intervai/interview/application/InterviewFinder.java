package wlsh.project.intervai.interview.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.infra.InterviewRepository;

@Component
@RequiredArgsConstructor
public class InterviewFinder {

    private final InterviewRepository interviewRepository;

    public Interview find(Long interviewId) {
        return interviewRepository.findByIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_FOUND))
                .toDomain();
    }
}
