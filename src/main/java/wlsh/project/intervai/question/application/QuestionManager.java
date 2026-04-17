package wlsh.project.intervai.question.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.question.infra.QuestionEntity;
import wlsh.project.intervai.question.infra.QuestionRepository;

@Component
@RequiredArgsConstructor
public class QuestionManager {

    private final QuestionRepository questionRepository;

    @Transactional
    public Question create(Long interviewId, Long sessionId,
                           String content, QuestionType questionType, int questionIndex) {
        Question question = Question.create(interviewId, sessionId, content, questionType, questionIndex);
        QuestionEntity entity = questionRepository.save(QuestionEntity.from(question));
        return entity.toDomain();
    }

    @Transactional
    public Optional<Question> createFollowUp(Long interviewId, Long sessionId, Long parentQuestionId, String content) {
        if (content == null || content.isBlank()) {
            return Optional.empty();
        }
        Question question = Question.createFollowUp(interviewId, sessionId, parentQuestionId, content);
        QuestionEntity entity = questionRepository.save(QuestionEntity.from(question));
        return Optional.of(entity.toDomain());
    }

    @Transactional
    public List<Question> createAll(Long interviewId, Long sessionId, List<String> contents) {
        List<QuestionEntity> entities = new ArrayList<>();
        for (int i = 0; i < contents.size(); i++) {
            Question question = Question.create(interviewId, sessionId,
                    contents.get(i), QuestionType.QUESTION, i);
            entities.add(QuestionEntity.from(question));
        }
        return questionRepository.saveAll(entities).stream()
                .map(QuestionEntity::toDomain)
                .toList();
    }
}
