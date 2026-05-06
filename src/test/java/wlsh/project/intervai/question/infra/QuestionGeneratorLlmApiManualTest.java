package wlsh.project.intervai.question.infra;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;
import wlsh.project.intervai.question.application.QuestionGenerator;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.domain.SessionStatus;

@SpringBootTest
@ActiveProfiles({"prod", "local-gemini"})
@EnabledIfEnvironmentVariable(named = "RUN_LLM_API_TESTS", matches = "true")
class QuestionGeneratorLlmApiManualTest {

    @Autowired
    private QuestionGenerator questionGenerator;

    @Test
    @DisplayName("실제 GitHub 저장소 분석 결과를 포함해 LLM API로 포트폴리오 질문을 생성한다")
    void generatePortfolioQuestionsWithLlmApi() {
        Interview interview = Interview.of(
                43L,
                1L,
                JobCategory.BACKEND,
                InterviewType.PORTFOLIO,
                Difficulty.JUNIOR,
                5,
                3,
                InterviewerTone.NORMAL,
                List.of(),
                List.of("https://github.com/wlsh44/IntervAI"),
                List.of("Java", "Spring Boot", "SpringAI")
        );
        InterviewSession session = InterviewSession.of(
                43L,
                interview.getId(),
                interview.getUserId(),
                SessionStatus.IN_PROGRESS,
                0,
                0,
                null
        );

        List<String> questions = questionGenerator.generateAll(interview, session);

        assertThat(questions)
                .hasSizeGreaterThanOrEqualTo(1)
                .allSatisfy(question -> assertThat(question).isNotBlank());
        questions.forEach(question -> System.out.println("[LLM 질문] " + question));
    }
}
