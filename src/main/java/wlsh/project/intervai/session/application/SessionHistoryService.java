package wlsh.project.intervai.session.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import wlsh.project.intervai.question.domain.QuestionType;
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

@Slf4j
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
        // ORDER BY id DESC ensures deterministic selection of the most recent feedback row
        Map<Long, String> feedbackByAnswerId = feedbackRepository
                .findByAnswerIdInAndStatusOrderByIdDesc(answerIds, EntityStatus.ACTIVE).stream()
                .collect(Collectors.toMap(
                        FeedbackEntity::getAnswerId,
                        FeedbackEntity::getFeedbackContent,
                        (mostRecent, older) -> mostRecent  // first = highest id (most recent)
                ));

        // Build entries in answer submission order (= actual interview sequence)
        Set<Long> answeredQuestionIds = new LinkedHashSet<>();
        List<SessionHistoryEntryResult> entries = new ArrayList<>();

        for (AnswerEntity answer : sortedAnswers) {
            Long questionId = answer.getQuestionId();
            if (!answeredQuestionIds.add(questionId)) {
                // Duplicate answer row detected — unique constraint should prevent this going forward
                log.warn("Duplicate answer row detected for questionId={}, sessionId={}. Keeping first row.",
                        questionId, session.getId());
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

        // Append unanswered questions in interview progression order:
        // current pending question first, then future main questions
        appendUnansweredQuestions(session, allQuestions, answeredQuestionIds, entries);

        return new SessionHistoryResult(
                session.getId(),
                session.getSessionStatus(),
                interview.getQuestionCount(),
                entries
        );
    }

    private void appendUnansweredQuestions(
            InterviewSessionEntity session,
            List<QuestionEntity> allQuestions,
            Set<Long> answeredQuestionIds,
            List<SessionHistoryEntryResult> entries) {

        QuestionEntity currentQuestion = findCurrentPendingQuestion(session, allQuestions, answeredQuestionIds);

        if (currentQuestion != null) {
            entries.add(toEntry(currentQuestion));
        }

        // Remaining future questions (excluding current)
        for (QuestionEntity q : allQuestions) {
            if (!answeredQuestionIds.contains(q.getId())
                    && (currentQuestion == null || !q.getId().equals(currentQuestion.getId()))) {
                entries.add(toEntry(q));
            }
        }
    }

    /**
     * Identifies the question the user is currently expected to answer,
     * using session state to distinguish it from future (not-yet-asked) questions.
     */
    private QuestionEntity findCurrentPendingQuestion(
            InterviewSessionEntity session,
            List<QuestionEntity> allQuestions,
            Set<Long> answeredQuestionIds) {

        if (session.getFollowUpCount() > 0) {
            // A follow-up is in progress: the latest unanswered FOLLOW_UP is current
            return allQuestions.stream()
                    .filter(q -> q.getQuestionType() == QuestionType.FOLLOW_UP
                            && !answeredQuestionIds.contains(q.getId()))
                    .max(Comparator.comparingLong(QuestionEntity::getId))
                    .orElse(null);
        }

        // Current is the main question at currentMainQuestionIdx
        int idx = session.getCurrentMainQuestionIdx();
        return allQuestions.stream()
                .filter(q -> q.getQuestionType() == QuestionType.QUESTION
                        && q.getQuestionIndex() == idx
                        && !answeredQuestionIds.contains(q.getId()))
                .findFirst()
                .orElse(null);
    }

    private SessionHistoryEntryResult toEntry(QuestionEntity q) {
        return new SessionHistoryEntryResult(q.getId(), q.getContent(), q.getQuestionType(), null, null);
    }
}
