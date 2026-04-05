package wlsh.project.intervai.question.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.NextQuestionResult;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.infra.QuestionEntity;
import wlsh.project.intervai.question.infra.QuestionRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionFinder {

    private final QuestionRepository questionRepository;

    public Question find(Long questionId) {
        return questionRepository.findByIdAndStatus(questionId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND))
                .toDomain();
    }

    public NextQuestionResult findAndNext(Long sessionId, Integer questionIdx) {
        List<QuestionEntity> questions = questionRepository.findBySessionIdAndStatusOrderByQuestionIndexAsc(sessionId, EntityStatus.ACTIVE);

        Question currentQuestion = getQuestion(questionIdx, questions);
        boolean hasNext = questions.size() > currentQuestion.getQuestionIndex() + 1;
        return new NextQuestionResult(currentQuestion, hasNext);
    }

    private Question getQuestion(Integer questionIdx, List<QuestionEntity> questions) {
        QuestionEntity currentQuestion;
        if (questionIdx == null) {
            currentQuestion = questions.getFirst();
        } else {
            currentQuestion = questions.get(questionIdx + 1);
        }
        return currentQuestion.toDomain();
    }
}
