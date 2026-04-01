package wlsh.project.intervai.question.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.infra.QuestionRepository;

@Component
@RequiredArgsConstructor
public class QuestionFinder {

    private final QuestionRepository questionRepository;

    public int countBySessionId(Long sessionId) {
        return questionRepository.countBySessionIdAndStatus(sessionId, EntityStatus.ACTIVE);
    }

    public Question find(Long questionId) {
        return questionRepository.findByIdAndStatus(questionId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND))
                .toDomain();
    }
}
