package wlsh.project.intervai.question.application;

import wlsh.project.intervai.interview.domain.Interview;

public interface QuestionGenerator {

    String generate(Interview interview);
}
