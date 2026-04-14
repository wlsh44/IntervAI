package wlsh.project.intervai.session.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.answer.infra.AnswerEntity;
import wlsh.project.intervai.answer.infra.AnswerRepository;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.feedback.infra.FeedbackEntity;
import wlsh.project.intervai.feedback.infra.FeedbackRepository;
import wlsh.project.intervai.interview.infra.InterviewEntity;
import wlsh.project.intervai.interview.infra.InterviewRepository;
import wlsh.project.intervai.question.infra.QuestionEntity;
import wlsh.project.intervai.question.infra.QuestionRepository;
import wlsh.project.intervai.session.application.dto.SessionHistoryEntryResult;
import wlsh.project.intervai.session.application.dto.SessionHistoryResult;
import wlsh.project.intervai.session.infra.InterviewSessionEntity;
import wlsh.project.intervai.session.infra.InterviewSessionRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionHistoryService {

    private final InterviewSessionValidator interviewSessionValidator;
    private final InterviewRepository interviewRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final FeedbackRepository feedbackRepository;

    public SessionHistoryResult getHistory(Long userId, Long interviewId) {
        interviewSessionValidator.validateInterviewOwner(interviewId, userId);

        InterviewEntity interview = interviewRepository.findByIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_FOUND));

        InterviewSessionEntity session = interviewSessionRepository.findByInterviewIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        List<QuestionEntity> questions = questionRepository.findBySessionIdAndStatusOrderByIdAsc(
                session.getId(), EntityStatus.ACTIVE);

        List<AnswerEntity> answers = answerRepository.findBySessionIdAndStatus(
                session.getId(), EntityStatus.ACTIVE);

        Map<Long, String> answerByQuestionId = answers.stream()
                .collect(Collectors.toMap(AnswerEntity::getQuestionId, AnswerEntity::getContent));

        Map<Long, Long> answerIdByQuestionId = answers.stream()
                .collect(Collectors.toMap(AnswerEntity::getQuestionId, AnswerEntity::getId));

        List<Long> answerIds = answers.stream().map(AnswerEntity::getId).toList();
        Map<Long, String> feedbackByAnswerId = feedbackRepository.findByAnswerIdIn(answerIds).stream()
                .collect(Collectors.toMap(FeedbackEntity::getAnswerId, FeedbackEntity::getFeedbackContent));

        List<SessionHistoryEntryResult> entries = questions.stream()
                .map(q -> new SessionHistoryEntryResult(
                        q.getId(),
                        q.getContent(),
                        q.getQuestionType(),
                        answerByQuestionId.get(q.getId()),
                        answerIdByQuestionId.containsKey(q.getId())
                                ? feedbackByAnswerId.get(answerIdByQuestionId.get(q.getId()))
                                : null
                ))
                .toList();

        return new SessionHistoryResult(
                session.getId(),
                session.getSessionStatus(),
                interview.getQuestionCount(),
                entries
        );
    }
}
