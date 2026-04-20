package wlsh.project.intervai.report.application;

import org.springframework.stereotype.Component;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.session.domain.SessionHistory;

import java.util.Comparator;
import java.util.List;

@Component
public class ReportQuestionReader {

    public List<SessionHistory> readMainQuestions(List<SessionHistory> histories) {
        return histories.stream()
                .filter(history -> history.questionType() == QuestionType.QUESTION)
                .sorted(Comparator.comparing(SessionHistory::questionIndex))
                .toList();
    }
}
