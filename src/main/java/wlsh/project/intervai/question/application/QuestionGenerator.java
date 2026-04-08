package wlsh.project.intervai.question.application;

import java.util.List;
import wlsh.project.intervai.interview.domain.Interview;

public interface QuestionGenerator {

    List<String> generateAll(Interview interview);
}
