package wlsh.project.intervai.question.infra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;
import wlsh.project.intervai.question.domain.GithubRepositorySummary;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionPromptBuilderTest {

    private QuestionPromptBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new QuestionPromptBuilder(new ClassPathResource("prompts/question-generator.st"));
    }

    @Test
    @DisplayName("CS 면접 유형이면 CS 분야가 프롬프트에 포함된다")
    void buildWithCsType() {
        Interview interview = Interview.create(1L, new CreateInterviewCommand(
                JobCategory.BACKEND,
                InterviewType.CS,
                Difficulty.ENTRY,
                5,
                InterviewerTone.NORMAL,
                List.of(CsSubject.of(CsCategory.NETWORK, List.of("HTTP", "TCP"))),
                null,
                null));

        String prompt = builder.build(interview);

        assertThat(prompt).contains("CS 분야");
        assertThat(prompt).contains("NETWORK");
        assertThat(prompt).contains("HTTP");
        assertThat(prompt).doesNotContain("포트폴리오 링크");
    }

    @Test
    @DisplayName("포트폴리오 면접 유형이면 포트폴리오 링크가 프롬프트에 포함된다")
    void buildWithPortfolioType() {
        Interview interview = Interview.create(1L, new CreateInterviewCommand(
                JobCategory.BACKEND,
                InterviewType.PORTFOLIO,
                Difficulty.JUNIOR,
                3,
                InterviewerTone.FRIENDLY,
                null,
                List.of("https://github.com/user/repo"),
                null));

        String prompt = builder.build(interview);

        assertThat(prompt).contains("포트폴리오 링크");
        assertThat(prompt).contains("https://github.com/user/repo");
        assertThat(prompt).doesNotContain("CS 분야");
    }

    @Test
    @DisplayName("GitHub 저장소 분석 결과가 있으면 프롬프트에 포함된다")
    void buildWithGithubRepositorySummary() {
        Interview interview = Interview.create(1L, new CreateInterviewCommand(
                JobCategory.BACKEND,
                InterviewType.PORTFOLIO,
                Difficulty.JUNIOR,
                5,
                InterviewerTone.FRIENDLY,
                List.of(),
                List.of("https://github.com/user/repo"),
                List.of()));
        GithubRepositorySummary summary = GithubRepositorySummary.available(
                "https://github.com/user/repo",
                "user/repo",
                "면접 서비스",
                "main",
                "Java",
                List.of("Java", "Shell"),
                "Spring Boot와 LLM을 사용한 면접 서비스입니다.");

        String prompt = builder.build(interview, List.of(summary));

        assertThat(prompt)
                .contains("GitHub 저장소 분석")
                .contains("user/repo")
                .contains("면접 서비스")
                .contains("Java")
                .contains("Spring Boot와 LLM");
    }

    @Test
    @DisplayName("ALL 면접 유형이면 CS 분야와 포트폴리오 링크가 모두 프롬프트에 포함된다")
    void buildWithAllType() {
        Interview interview = Interview.create(1L, new CreateInterviewCommand(
                JobCategory.BACKEND,
                InterviewType.ALL,
                Difficulty.SENIOR,
                10,
                InterviewerTone.AGGRESSIVE,
                List.of(CsSubject.of(CsCategory.DATABASE, List.of("인덱스"))),
                List.of("https://github.com/user/repo"),
                null));

        String prompt = builder.build(interview);

        assertThat(prompt).contains("CS 분야");
        assertThat(prompt).contains("포트폴리오 링크");
    }

    @Test
    @DisplayName("토픽이 ALL이면 해당 CS 카테고리 전체 범위로 프롬프트에 반영된다")
    void buildWithAllCategoryTopic() {
        Interview interview = Interview.create(1L, new CreateInterviewCommand(
                JobCategory.BACKEND,
                InterviewType.CS,
                Difficulty.ENTRY,
                5,
                InterviewerTone.NORMAL,
                List.of(CsSubject.of(CsCategory.NETWORK, List.of(CsSubject.ALL_TOPICS))),
                null,
                null));

        String prompt = builder.build(interview);

        assertThat(prompt)
                .contains("NETWORK")
                .contains("네트워크 전체 범위에서 랜덤")
                .doesNotContain(CsSubject.ALL_TOPICS);
    }

    @Test
    @DisplayName("면접 설정값이 프롬프트에 반영된다")
    void buildContainsInterviewSettings() {
        Interview interview = Interview.create(1L, new CreateInterviewCommand(
                JobCategory.BACKEND,
                InterviewType.CS,
                Difficulty.ENTRY,
                5,
                InterviewerTone.NORMAL,
                List.of(CsSubject.of(CsCategory.NETWORK, List.of("HTTP"))),
                null,
                null));

        String prompt = builder.build(interview);

        assertThat(prompt)
                .contains(String.valueOf(interview.getQuestionCount()))
                .contains(interview.getInterviewType().getKo())
                .contains(interview.getDifficulty().getKo())
                .contains(interview.getInterviewerTone().getKo());
    }
}
