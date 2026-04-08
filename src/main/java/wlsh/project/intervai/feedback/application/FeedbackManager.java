package wlsh.project.intervai.feedback.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.feedback.domain.Feedback;
import wlsh.project.intervai.feedback.infra.FeedbackEntity;
import wlsh.project.intervai.feedback.infra.FeedbackRepository;

@Component
@RequiredArgsConstructor
public class FeedbackManager {

    private final FeedbackRepository feedbackRepository;

    public Feedback create(Long answerId, String feedbackContent) {
        Feedback feedback = Feedback.create(answerId, feedbackContent);
        FeedbackEntity entity = feedbackRepository.save(FeedbackEntity.from(feedback));
        return entity.toDomain();
    }
}
