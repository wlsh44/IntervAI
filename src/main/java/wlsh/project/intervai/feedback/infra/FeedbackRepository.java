package wlsh.project.intervai.feedback.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {

    List<FeedbackEntity> findByAnswerIdInAndStatusOrderByIdDesc(List<Long> answerIds, EntityStatus status);

    List<FeedbackEntity> findByAnswerIdInAndStatus(List<Long> answerIds, EntityStatus entityStatus);
}

