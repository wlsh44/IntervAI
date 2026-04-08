package wlsh.project.intervai.answer.application;

import wlsh.project.intervai.answer.application.dto.AnswerResultDto;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.question.domain.Question;

public interface AnswerResultGenerator {

    AnswerResultDto generate(String conversationId, Interview interview, Question question, Answer answer);
}
