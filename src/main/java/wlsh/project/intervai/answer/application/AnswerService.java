package wlsh.project.intervai.answer.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.answer.application.dto.AnswerResultDto;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.answer.domain.AnswerResult;
import wlsh.project.intervai.answer.domain.CreateAnswerCommand;
import wlsh.project.intervai.feedback.application.FeedbackManager;
import wlsh.project.intervai.interview.application.InterviewFinder;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.question.application.QuestionManager;
import wlsh.project.intervai.session.application.InterviewSessionFinder;
import wlsh.project.intervai.session.application.InterviewSessionManager;
import wlsh.project.intervai.session.domain.InterviewSession;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerValidator answerValidator;
    private final AnswerManager answerManager;
    private final AnswerHandler answerHandler;
    private final FeedbackManager feedbackManager;
    private final QuestionManager questionManager;
    private final InterviewFinder interviewFinder;
    private final InterviewSessionFinder interviewSessionFinder;
    private final InterviewSessionManager interviewSessionManager;

    public AnswerResult answer(Long userId, Long interviewId, Long questionId, CreateAnswerCommand command) {
        answerValidator.validate(userId, interviewId, questionId);

        Answer answer = answerManager.create(userId, questionId, command.content());
        AnswerResultDto answerResultDto = answerHandler.submit(questionId, answer);

        feedbackManager.create(answer.getId(), answerResultDto.feedback());
        decideNextQuestion(answer, answerResultDto.followUpQuestion());

        return AnswerResult.of(answerResultDto.feedback());
    }

    private void decideNextQuestion(Answer answer, String followUpQuestion) {
        InterviewSession session = interviewSessionFinder.find(answer.getSessionId());
        Interview interview = interviewFinder.find(answer.getInterviewId());

        boolean hasFollowUp = followUpQuestion != null && !followUpQuestion.isBlank();
        boolean withinLimit = session.getFollowUpCount() < interview.getMaxFollowUpCount();

        if (hasFollowUp && withinLimit) {
            questionManager.createFollowUp(answer.getInterviewId(), answer.getSessionId(), followUpQuestion);
            interviewSessionManager.addFollowUp(session.getId());
        } else {
            interviewSessionManager.advanceToNext(session.getId());
        }
    }
}
