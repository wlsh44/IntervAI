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

        return sessionQuestions.stream()
                .map(sessionHistoryDto ->
                        SessionHistory.createSessionHistory(sessionHistoryDto, feedbacks.get(sessionHistoryDto.getAnswerId())))
                .toList();
    }
}
