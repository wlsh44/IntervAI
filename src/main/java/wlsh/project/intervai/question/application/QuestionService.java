package wlsh.project.intervai.question.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.interview.application.InterviewFinder;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.NextQuestionResult;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.session.application.InterviewSessionFinder;
import wlsh.project.intervai.session.application.InterviewSessionValidator;
import wlsh.project.intervai.session.domain.InterviewSession;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final InterviewFinder interviewFinder;
    private final InterviewSessionFinder interviewSessionFinder;
    private final InterviewSessionValidator interviewSessionValidator;
    private final QuestionGenerator questionGenerator;
    private final QuestionManager questionManager;
    private final QuestionValidator questionValidator;
    private final QuestionFinder questionFinder;

    public List<Question> createAll(Long userId, Long interviewId) {
        interviewSessionValidator.validateInterviewOwner(interviewId, userId);
        Interview interview = interviewFinder.find(interviewId);
        InterviewSession session = interviewSessionFinder.findByInterviewId(interviewId);
        List<String> contents = questionGenerator.generateAll(interview);
        return questionManager.createAll(interviewId, session.getId(), contents);
    }

    public NextQuestionResult nextQuestion(Long userId, Long interviewId, Integer questionIdx) {
        questionValidator.validate(userId, interviewId, questionIdx);
        return questionFinder.findAndNext(interviewId, questionIdx);
    }
}
