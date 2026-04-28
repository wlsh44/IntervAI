package wlsh.project.intervai.question.application;

import java.util.List;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.session.domain.InterviewSession;

public interface QuestionGenerator {

    List<String> generateAll(Interview interview, InterviewSession session);
}
