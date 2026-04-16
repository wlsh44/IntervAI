package wlsh.project.intervai.session.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.feedback.infra.FeedbackEntity;
import wlsh.project.intervai.feedback.infra.FeedbackRepository;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.session.application.dto.SessionHistoryDto;
import wlsh.project.intervai.session.domain.SessionHistory;
import wlsh.project.intervai.session.infra.InterviewSessionRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SessionHistoryFinder {

    private final FeedbackRepository feedbackRepository;
    private final InterviewSessionRepository interviewSessionRepository;

    public List<SessionHistory> findSessionHistories(Long interviewId) {
        List<SessionHistoryDto> sessionQuestions = interviewSessionRepository.findSessionHistoryByInterviewId(interviewId, EntityStatus.ACTIVE);
        if (sessionQuestions.isEmpty()) {
            return List.of();
        }

        List<Long> answerIds = sessionQuestions.stream()
                .map(SessionHistoryDto::getAnswerId)
                .filter(Objects::nonNull)
                .toList();
        Map<Long, FeedbackEntity> feedbacks = feedbackRepository.findByAnswerIdInAndStatus(answerIds, EntityStatus.ACTIVE)
                .stream()
                .collect(Collectors.toMap(
                        FeedbackEntity::getAnswerId,
                        Function.identity(),
                        (existing, duplicate) -> existing
                ));

        List<SessionHistory> histories = sessionQuestions.stream()
                .map(sessionHistoryDto ->
                        SessionHistory.createSessionHistory(sessionHistoryDto, feedbacks.get(sessionHistoryDto.getAnswerId())))
                .toList();
        return sortByConversationFlow(histories);
    }

    private List<SessionHistory> sortByConversationFlow(List<SessionHistory> histories) {
        List<SessionHistory> answered = histories.stream()
                .filter(history -> history.answerId() != null)
                .sorted(Comparator.comparing(SessionHistory::answerId))
                .toList();

        List<SessionHistory> unanswered = sortUnansweredByStructure(histories.stream()
                .filter(history -> history.answerId() == null)
                .toList());

        List<SessionHistory> ordered = new ArrayList<>(answered.size() + unanswered.size());
        ordered.addAll(answered);
        ordered.addAll(unanswered);
        return ordered;
    }

    private List<SessionHistory> sortUnansweredByStructure(List<SessionHistory> unanswered) {
        Map<Long, List<SessionHistory>> childrenByParentId = unanswered.stream()
                .filter(history -> history.parentQuestionId() != null)
                .collect(Collectors.groupingBy(SessionHistory::parentQuestionId));

        childrenByParentId.values()
                .forEach(children -> children.sort(Comparator.comparing(SessionHistory::questionId)));

        List<SessionHistory> roots = unanswered.stream()
                .filter(history -> history.parentQuestionId() == null)
                .sorted(Comparator
                        .comparingInt((SessionHistory history) -> history.questionType() == QuestionType.QUESTION ? 0 : 1)
                        .thenComparing(history -> history.questionType() == QuestionType.QUESTION
                                ? history.questionIndex()
                                : Integer.MAX_VALUE)
                        .thenComparing(SessionHistory::questionId))
                .toList();

        List<SessionHistory> ordered = new ArrayList<>(unanswered.size());
        for (SessionHistory root : roots) {
            appendDepthFirst(root, childrenByParentId, ordered);
        }
        return ordered;
    }

    private void appendDepthFirst(
            SessionHistory current,
            Map<Long, List<SessionHistory>> childrenByParentId,
            List<SessionHistory> ordered
    ) {
        ordered.add(current);
        for (SessionHistory child : childrenByParentId.getOrDefault(current.questionId(), List.of())) {
            appendDepthFirst(child, childrenByParentId, ordered);
        }
    }
}
