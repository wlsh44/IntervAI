package wlsh.project.intervai.question.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wlsh.project.intervai.common.ai.AiChatCaller;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;
import wlsh.project.intervai.question.application.GithubRepositoryReader;
import wlsh.project.intervai.question.domain.GithubRepositorySummary;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.domain.SessionStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApiQuestionGeneratorTest {

    @Mock
    private AiChatCaller aiChatCaller;

    @Mock
    private QuestionPromptBuilder promptBuilder;

    @Mock
    private GithubRepositoryReader githubRepositoryReader;

    private ApiQuestionGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ApiQuestionGenerator(aiChatCaller, promptBuilder, githubRepositoryReader, new ObjectMapper());
    }

    private final Interview interview = Interview.create(1L, new CreateInterviewCommand(
            JobCategory.BACKEND,
            InterviewType.CS,
            Difficulty.ENTRY,
            3,
            InterviewerTone.NORMAL,
            List.of(CsSubject.of(CsCategory.NETWORK, List.of("HTTP"))),
            null,
            null));

    private final InterviewSession session = InterviewSession.of(
            10L,
            interview.getId(),
            1L,
            SessionStatus.IN_PROGRESS,
            0,
            0,
            null
    );

    @Test
    @DisplayName("정상 JSON 응답이면 파싱된 질문 리스트를 반환한다")
    void generateAll_returnsQuestions() {
        given(githubRepositoryReader.read(interview.getPortfolioLinks())).willReturn(List.of());
        given(promptBuilder.build(interview, List.of())).willReturn("some prompt");
        given(aiChatCaller.callWithSession("10", "some prompt")).willReturn("""
                ["질문1", "질문2", "질문3"]
                """);

        List<String> questions = generator.generateAll(interview, session);

        assertThat(questions).containsExactly("질문1", "질문2", "질문3");
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 원본 응답 문자열을 단일 리스트로 반환한다")
    void generateAll_fallbackOnParseError() {
        given(githubRepositoryReader.read(interview.getPortfolioLinks())).willReturn(List.of());
        given(promptBuilder.build(interview, List.of())).willReturn("some prompt");
        given(aiChatCaller.callWithSession("10", "some prompt")).willReturn("파싱 불가 응답");

        List<String> questions = generator.generateAll(interview, session);

        assertThat(questions).containsExactly("파싱 불가 응답");
    }

    @Test
    @DisplayName("promptBuilder와 aiChatCaller를 순서대로 호출한다")
    void generateAll_callsBuilderThenCaller() {
        given(githubRepositoryReader.read(interview.getPortfolioLinks())).willReturn(List.of());
        given(promptBuilder.build(interview, List.of())).willReturn("built prompt");
        given(aiChatCaller.callWithSession("10", "built prompt")).willReturn("[]");

        generator.generateAll(interview, session);

        InOrder inOrder = inOrder(githubRepositoryReader, promptBuilder, aiChatCaller);
        inOrder.verify(githubRepositoryReader).read(interview.getPortfolioLinks());
        inOrder.verify(promptBuilder).build(interview, List.of());
        inOrder.verify(aiChatCaller).callWithSession("10", "built prompt");
    }

    @Test
    @DisplayName("GitHub 저장소 분석 결과를 프롬프트 빌더에 전달한다")
    void generateAll_passesGithubRepositorySummaries() {
        Interview portfolioInterview = Interview.create(1L, new CreateInterviewCommand(
                JobCategory.BACKEND,
                InterviewType.PORTFOLIO,
                Difficulty.ENTRY,
                5,
                InterviewerTone.NORMAL,
                List.of(),
                List.of("https://github.com/user/repo"),
                List.of()));
        List<GithubRepositorySummary> summaries = List.of(GithubRepositorySummary.available(
                "https://github.com/user/repo",
                "user/repo",
                "테스트 저장소",
                "main",
                "Java",
                List.of("Java", "Shell"),
                "Spring Boot README"));
        given(githubRepositoryReader.read(portfolioInterview.getPortfolioLinks())).willReturn(summaries);
        given(promptBuilder.build(portfolioInterview, summaries)).willReturn("portfolio prompt");
        given(aiChatCaller.callWithSession("10", "portfolio prompt")).willReturn("[]");

        generator.generateAll(portfolioInterview, session);

        verify(promptBuilder).build(portfolioInterview, summaries);
    }
}
