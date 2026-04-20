package wlsh.project.intervai.feedback.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.feedback.infra.FeedbackEntity;
import wlsh.project.intervai.feedback.infra.FeedbackRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeedbackScoreFinder {

    private final FeedbackRepository feedbackRepository;

    public Map<Long, Integer> findScoresByAnswerIds(List<Long> answerIds) {
        if (answerIds.isEmpty()) {
            return Map.of();
        }
        return feedbackRepository.findByAnswerIdInAndStatus(answerIds, EntityStatus.ACTIVE).stream()
                .collect(Collectors.toMap(
                        FeedbackEntity::getAnswerId,
                        FeedbackEntity::getScore,
                        (existing, duplicate) -> existing
                ));
    }
}
