package wlsh.project.intervai.interview.infra;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;

public interface InterviewPortfolioLinkRepository extends JpaRepository<InterviewPortfolioLinkEntity, Long> {

    List<InterviewPortfolioLinkEntity> findByInterviewIdAndStatusOrderByIdAsc(Long interviewId, EntityStatus status);
}
