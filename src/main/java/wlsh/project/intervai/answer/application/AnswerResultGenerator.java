package wlsh.project.intervai.answer.application;

import wlsh.project.intervai.answer.application.dto.AnswerResultDto;
import wlsh.project.intervai.interview.domain.Interview;

public interface AnswerResultGenerator {

    AnswerResultDto generate(String conversationId, Interview interview, String question, String answer);
}
