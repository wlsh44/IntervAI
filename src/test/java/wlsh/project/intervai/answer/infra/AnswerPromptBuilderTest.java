package wlsh.project.intervai.answer.infra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.domain.QuestionType;

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

        String prompt = builder.build(question, answer);

        assertThat(prompt).contains("TCP와 UDP의 차이를 설명하세요.");
        assertThat(prompt).contains("TCP는 연결 지향이고 UDP는 비연결입니다.");
    }
}
