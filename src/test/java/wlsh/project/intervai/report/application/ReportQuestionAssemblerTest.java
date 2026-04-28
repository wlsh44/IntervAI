package wlsh.project.intervai.report.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wlsh.project.intervai.feedback.application.FeedbackScoreFinder;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.report.application.dto.ReportGenerationResultDto;
import wlsh.project.intervai.report.application.dto.ReportGenerationResultDto.QuestionKeywords;
import wlsh.project.intervai.report.domain.ReportQuestion;
import wlsh.project.intervai.session.application.SessionHistoryFinder;
import wlsh.project.intervai.session.domain.SessionHistory;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ReportQuestionAssemblerTest {

    private final SessionHistoryFinder sessionHistoryFinder = mock(SessionHistoryFinder.class);
    private final FeedbackScoreFinder feedbackScoreFinder = mock(FeedbackScoreFinder.class);
    private final ReportQuestionAssembler assembler = new ReportQuestionAssembler(
            sessionHistoryFinder,
            feedbackScoreFinder,
            new ReportQuestionReader()
    );

    @Test
    @DisplayName("LLM 키워드에 중복 questionId가 있어도 병합해 리포트를 조립한다")
    void assembleMergesDuplicateQuestionKeywords() {
        Long interviewId = 1L;
        given(sessionHistoryFinder.findSessionHistories(interviewId)).willReturn(List.of(
                new SessionHistory(
                        10L,
                        null,
                        20L,
                        "질문",
                        "답변",
                        "피드백",
                        80,
                        QuestionType.QUESTION,
                        0
                )
        ));
        given(feedbackScoreFinder.findScoresByAnswerIds(List.of(20L))).willReturn(Map.of(20L, 80));
        ReportGenerationResultDto result = new ReportGenerationResultDto(
                90,
                "총평",
                List.of(
                        new QuestionKeywords(10L, List.of("JPA", "ORM")),
                        new QuestionKeywords(10L, List.of("ORM", "ENTITY"))
                )
        );

        List<ReportQuestion> questions = assembler.assemble(interviewId, result);

        assertThat(questions).hasSize(1);
        assertThat(questions.getFirst().keywords()).containsExactly("JPA", "ORM", "ENTITY");
    }
}
