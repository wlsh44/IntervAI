package wlsh.project.intervai.answer.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.answer.application.dto.AnswerResultDto;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.answer.domain.AnswerResult;
import wlsh.project.intervai.answer.domain.CreateAnswerCommand;
import java.util.Optional;
import wlsh.project.intervai.feedback.application.FeedbackManager;
import wlsh.project.intervai.question.application.QuestionFinder;
import wlsh.project.intervai.question.application.QuestionManager;
import wlsh.project.intervai.question.domain.Question;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerValidator answerValidator;
    private final AnswerManager answerManager;
    private final AnswerHandler answerHandler;
    private final FeedbackManager feedbackManager;
    private final QuestionManager questionManager;
    private final QuestionFinder questionFinder;

    public AnswerResult answer(Long userId, Long interviewId, Long questionId, CreateAnswerCommand command) {
        answerValidator.validate(userId, interviewId, questionId);

        Question question = questionFinder.find(questionId);
        Answer answer = answerManager.create(userId, questionId, command.content());
        AnswerResultDto answerResultDto = answerHandler.submit(questionId, answer);

        feedbackManager.create(answer.getId(), answerResultDto.feedback());
        Optional<Question> followUp = questionManager.createFollowUp(
                question.getInterviewId(), question.getSessionId(), answerResultDto.followUpQuestion());
        Long followUpQuestionId = followUp.map(Question::getId).orElse(null);

        return AnswerResult.of(answer.getId(), answerResultDto.feedback(),
                followUpQuestionId, answerResultDto.followUpQuestion());
    }
}
