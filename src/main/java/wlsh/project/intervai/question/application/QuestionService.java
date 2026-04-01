package wlsh.project.intervai.question.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.interview.application.InterviewFinder;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.session.application.InterviewSessionFinder;
import wlsh.project.intervai.session.domain.InterviewSession;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final InterviewSessionFinder interviewSessionFinder;
    private final InterviewFinder interviewFinder;
    private final QuestionValidator questionValidator;
    private final QuestionGenerator questionGenerator;
    private final QuestionManager questionManager;

    public Question create(Long sessionId) {
        InterviewSession session = interviewSessionFinder.find(sessionId);
        Interview interview = interviewFinder.find(session.getInterviewId());
        questionValidator.validate(session, interview);
        String content = questionGenerator.generate(interview);
        return questionManager.create(session.getUserId(), interview.getId(), sessionId, content);
    }
}
