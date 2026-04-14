package wlsh.project.intervai.feedback.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {

    List<FeedbackEntity> findByAnswerIdIn(List<Long> answerIds);
}

