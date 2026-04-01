package wlsh.project.intervai.question.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.infra.QuestionEntity;
import wlsh.project.intervai.question.infra.QuestionRepository;

@Component
@RequiredArgsConstructor
public class QuestionManager {

    private final QuestionRepository questionRepository;

    public Question create(Long userId, Long interviewId, Long sessionId, String content) {
        Question question = Question.create(userId, interviewId, sessionId, content);
        QuestionEntity entity = questionRepository.save(QuestionEntity.from(question));
        return entity.toDomain();
    }
}