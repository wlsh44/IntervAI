package wlsh.project.intervai.answer.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.answer.infra.AnswerEntity;
import wlsh.project.intervai.answer.infra.AnswerRepository;

@Component
@RequiredArgsConstructor
public class AnswerManager {

    private final AnswerRepository answerRepository;

    public Answer create(Long userId, Long interviewId, Long sessionId, Long questionId, String content) {
        Answer answer = Answer.create(userId, interviewId, sessionId, questionId, content);
        AnswerEntity entity = answerRepository.save(AnswerEntity.from(answer));
        return entity.toDomain();
    }
}
