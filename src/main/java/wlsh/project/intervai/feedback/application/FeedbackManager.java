package wlsh.project.intervai.feedback.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.feedback.domain.Feedback;
import wlsh.project.intervai.feedback.infra.FeedbackEntity;
import wlsh.project.intervai.feedback.infra.FeedbackRepository;

@Component
@RequiredArgsConstructor
public class FeedbackManager {

    private final FeedbackRepository feedbackRepository;

    public Feedback create(Long answerId, String feedbackContent, int score) {
        Feedback feedback = Feedback.create(answerId, feedbackContent, score);
        FeedbackEntity entity = feedbackRepository.save(FeedbackEntity.from(feedback));
        return entity.toDomain();
    }

    @Transactional
    public void deleteByAnswerIds(List<Long> answerIds) {
        if (answerIds.isEmpty()) {
            return;
        }
        feedbackRepository.findByAnswerIdInAndStatus(answerIds, EntityStatus.ACTIVE)
                .forEach(FeedbackEntity::delete);
    }
}
