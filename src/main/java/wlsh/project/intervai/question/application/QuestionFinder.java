package wlsh.project.intervai.question.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.question.infra.QuestionRepository;

@Component
@RequiredArgsConstructor
public class QuestionFinder {

    private final QuestionRepository questionRepository;

    public int countBySessionId(Long sessionId) {
        return questionRepository.countBySessionIdAndStatus(sessionId, EntityStatus.ACTIVE);
    }
}
