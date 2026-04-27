package wlsh.project.intervai.question.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApiQuestionGeneratorTest {

    @Mock
    private AiChatCaller aiChatCaller;

    @Mock
    private QuestionPromptBuilder promptBuilder;

    private ApiQuestionGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ApiQuestionGenerator(aiChatCaller, promptBuilder, new ObjectMapper());
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

    @Test
    @DisplayName("정상 JSON 응답이면 파싱된 질문 리스트를 반환한다")
    void generateAll_returnsQuestions() {
        given(promptBuilder.build(interview)).willReturn("some prompt");
        given(aiChatCaller.call("some prompt")).willReturn("""
                ["질문1", "질문2", "질문3"]
                """);

        List<String> questions = generator.generateAll(interview);

        assertThat(questions).containsExactly("질문1", "질문2", "질문3");
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 원본 응답 문자열을 단일 리스트로 반환한다")
    void generateAll_fallbackOnParseError() {
        given(promptBuilder.build(interview)).willReturn("some prompt");
        given(aiChatCaller.call(anyString())).willReturn("파싱 불가 응답");

        List<String> questions = generator.generateAll(interview);

        assertThat(questions).containsExactly("파싱 불가 응답");
    }

    @Test
    @DisplayName("promptBuilder와 aiChatCaller를 순서대로 호출한다")
    void generateAll_callsBuilderThenCaller() {
        given(promptBuilder.build(interview)).willReturn("built prompt");
        given(aiChatCaller.call("built prompt")).willReturn("[]");

        generator.generateAll(interview);

        verify(promptBuilder).build(interview);
        verify(aiChatCaller).call("built prompt");
    }
}
