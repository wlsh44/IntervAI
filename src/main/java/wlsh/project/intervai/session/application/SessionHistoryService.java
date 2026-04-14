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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        List<QuestionEntity> allQuestions = questionRepository.findBySessionIdAndStatusOrderByIdAsc(
                session.getId(), EntityStatus.ACTIVE);
        Map<Long, QuestionEntity> questionById = allQuestions.stream()
                .collect(Collectors.toMap(QuestionEntity::getId, q -> q));

        // Sort by id to get answer submission order (= actual interview order)
        List<AnswerEntity> sortedAnswers = answerRepository.findBySessionIdAndStatus(
                        session.getId(), EntityStatus.ACTIVE).stream()
                .sorted(Comparator.comparingLong(AnswerEntity::getId))
                .toList();

        List<Long> answerIds = sortedAnswers.stream().map(AnswerEntity::getId).toList();
        // P2: use merge function to tolerate duplicate feedback rows
        Map<Long, String> feedbackByAnswerId = feedbackRepository.findByAnswerIdInAndStatus(answerIds, EntityStatus.ACTIVE).stream()
                .collect(Collectors.toMap(
                        FeedbackEntity::getAnswerId,
                        FeedbackEntity::getFeedbackContent,
                        (existing, replacement) -> existing
                ));

        // P1: build entries in answer submission order (= actual interview sequence)
        Set<Long> answeredQuestionIds = new LinkedHashSet<>();
        List<SessionHistoryEntryResult> entries = new ArrayList<>();

        for (AnswerEntity answer : sortedAnswers) {
            Long questionId = answer.getQuestionId();
            // P2: skip duplicate answer rows for the same question
            if (!answeredQuestionIds.add(questionId)) {
                continue;
            }
            QuestionEntity q = questionById.get(questionId);
            if (q != null) {
                entries.add(new SessionHistoryEntryResult(
                        q.getId(),
                        q.getContent(),
                        q.getQuestionType(),
                        answer.getContent(),
                        feedbackByAnswerId.get(answer.getId())
                ));
            }
        }

        // Append unanswered questions (e.g. current question in an IN_PROGRESS session)
        for (QuestionEntity q : allQuestions) {
            if (!answeredQuestionIds.contains(q.getId())) {
                entries.add(new SessionHistoryEntryResult(
                        q.getId(),
                        q.getContent(),
                        q.getQuestionType(),
                        null,
                        null
                ));
            }
        }

        return new SessionHistoryResult(
                session.getId(),
                session.getSessionStatus(),
                interview.getQuestionCount(),
                entries
        );
    }
}
