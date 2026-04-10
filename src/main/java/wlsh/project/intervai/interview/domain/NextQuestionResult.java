package wlsh.project.intervai.interview.domain;

import wlsh.project.intervai.question.domain.Question;

public record NextQuestionResult(
        Question question,
        boolean hasNext
) {
}
