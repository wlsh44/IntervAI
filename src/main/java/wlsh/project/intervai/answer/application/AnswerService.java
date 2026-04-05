package wlsh.project.intervai.answer.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.answer.application.dto.AnswerResultDto;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.answer.domain.AnswerResult;
import wlsh.project.intervai.answer.domain.CreateAnswerCommand;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerValidator answerValidator;
    private final AnswerManager answerManager;
    private final AnswerHandler answerHandler;

    public AnswerResult answer(Long userId, Long interviewId, Long questionId, CreateAnswerCommand command) {
        answerValidator.validate(userId, interviewId, questionId);

        Answer answer = answerManager.create(userId, questionId, command.content());
        AnswerResultDto answerResultDto = answerHandler.submit(questionId, answer);

        return AnswerResult.of(answer.getId(), answerResultDto.feedback(), answerResultDto.followUpQuestion());
    }
}
