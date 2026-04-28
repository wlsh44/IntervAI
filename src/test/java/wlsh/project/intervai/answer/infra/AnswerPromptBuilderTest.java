package wlsh.project.intervai.answer.infra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.domain.QuestionType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnswerPromptBuilderTest {

    private AnswerPromptBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new AnswerPromptBuilder(new ClassPathResource("prompts/feedback-followup.st"));
    }

    @Test
    @DisplayName("질문과 답변 내용이 프롬프트에 포함된다")
    void buildContainsQuestionAndAnswer() {
        Question question = Question.create(1L, 1L, "TCP와 UDP의 차이를 설명하세요.", QuestionType.QUESTION, 1);
        Answer answer = Answer.create(1L, 1L, 1L, 1L, "TCP는 연결 지향이고 UDP는 비연결입니다.");
        Interview interview = Interview.create(1L, new CreateInterviewCommand(
                JobCategory.BACKEND,
                InterviewType.CS,
                Difficulty.JUNIOR,
                5,
                InterviewerTone.NORMAL,
                List.of(),
                List.of(),
                List.of()
        ));

        String prompt = builder.build(question, answer, interview);

        assertThat(prompt).contains("TCP와 UDP의 차이를 설명하세요.");
        assertThat(prompt).contains("TCP는 연결 지향이고 UDP는 비연결입니다.");
        assertThat(prompt).contains(JobCategory.BACKEND.name());
        assertThat(prompt).contains(Difficulty.JUNIOR.getKo());
    }
}
