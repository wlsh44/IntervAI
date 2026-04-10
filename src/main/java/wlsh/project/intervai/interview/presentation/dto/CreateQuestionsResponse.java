package wlsh.project.intervai.interview.presentation.dto;

import java.util.List;
import wlsh.project.intervai.question.domain.Question;

public record CreateQuestionsResponse(
        List<QuestionItem> questions
) {
    public record QuestionItem(
            Long questionId,
            String content,
            int questionIndex
    ) {
    }

    public static CreateQuestionsResponse of(List<Question> questions) {
        List<QuestionItem> items = questions.stream()
                .map(q -> new QuestionItem(q.getId(), q.getContent(), q.getQuestionIndex()))
                .toList();
        return new CreateQuestionsResponse(items);
    }
}
