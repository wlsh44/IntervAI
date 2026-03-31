package wlsh.project.intervai.session.infra;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSessionRepository extends JpaRepository<InterviewSessionEntity, Long> {
}
