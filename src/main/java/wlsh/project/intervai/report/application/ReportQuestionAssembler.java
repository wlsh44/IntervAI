package wlsh.project.intervai.report.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.feedback.application.FeedbackScoreFinder;
import wlsh.project.intervai.report.application.dto.ReportGenerationResultDto;
import wlsh.project.intervai.report.application.dto.ReportGenerationResultDto.QuestionKeywords;
import wlsh.project.intervai.report.domain.ReportQuestion;
import wlsh.project.intervai.session.application.SessionHistoryFinder;
import wlsh.project.intervai.session.domain.SessionHistory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReportQuestionAssembler {

    private final SessionHistoryFinder sessionHistoryFinder;
    private final FeedbackScoreFinder feedbackScoreFinder;
    private final ReportQuestionReader reportQuestionReader;

    public List<ReportQuestion> assemble(Long interviewId, ReportGenerationResultDto result) {
        List<SessionHistory> histories = sessionHistoryFinder.findSessionHistories(interviewId);
        List<SessionHistory> mainQuestions = reportQuestionReader.readMainQuestions(histories);
        Map<Long, List<String>> keywordsByQuestionId = mapKeywordsByQuestionId(result.questions());
        Map<Long, Integer> scoresByAnswerId = findScoresByAnswerIds(mainQuestions);

        return mainQuestions.stream()
                .map(history -> new ReportQuestion(
                        history.questionId(),
                        history.questionIndex(),
                        history.questionContent(),
                        history.answerContent(),
                        history.feedbackContent(),
                        history.answerId() == null ? null : scoresByAnswerId.get(history.answerId()),
                        keywordsByQuestionId.getOrDefault(history.questionId(), List.of())
                ))
                .toList();
    }

    private Map<Long, List<String>> mapKeywordsByQuestionId(List<QuestionKeywords> questions) {
        return questions.stream()
                .collect(Collectors.toMap(QuestionKeywords::questionId, QuestionKeywords::keywords));
    }

    private Map<Long, Integer> findScoresByAnswerIds(List<SessionHistory> mainQuestions) {
        List<Long> answerIds = mainQuestions.stream()
                .map(SessionHistory::answerId)
                .filter(Objects::nonNull)
                .toList();
        return feedbackScoreFinder.findScoresByAnswerIds(answerIds);
    }
}
