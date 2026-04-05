package wlsh.project.intervai.question.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.infra.InterviewEntity;
import wlsh.project.intervai.interview.infra.InterviewRepository;
import wlsh.project.intervai.session.infra.InterviewSessionEntity;
import wlsh.project.intervai.session.infra.InterviewSessionRepository;

@Component
@RequiredArgsConstructor
public class QuestionValidator {

    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewRepository interviewRepository;

    /**
     * 1. 세션 주인 체크
     * 2. 세션 진행 중인지 체크
     * 3. 넘어온 questionIdx가 미리 생성한 면접 질문 개수 questionCount 넘어가는지 체크
     */
    public void validate(Long userId, Long interviewId, Integer questionIdx) {
        InterviewEntity interview = interviewRepository.findByIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_FOUND));
        InterviewSessionEntity session = interviewSessionRepository.findByInterviewIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        validateSessionOwner(userId, session);
        validateSessionInProgress(session);
        validateQuestionCount(questionIdx, interview);
    }

    private void validateQuestionCount(Integer questionIdx, InterviewEntity interview) {
        if (questionIdx >= interview.getQuestionCount()) {
            throw new CustomException(ErrorCode.QUESTION_COUNT_EXCEEDED);
        }
    }

    private void validateSessionOwner(Long userId, InterviewSessionEntity session) {
        if (!session.isOwner(userId)) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }
    }

    private void validateSessionInProgress(InterviewSessionEntity session) {
        if (!session.isInProgress()) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_COMPLETED);
        }
    }

}
