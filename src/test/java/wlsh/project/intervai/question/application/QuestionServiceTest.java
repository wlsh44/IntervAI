package wlsh.project.intervai.question.application;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.application.InterviewManager;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerPersonality;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.session.application.InterviewSessionManager;
import wlsh.project.intervai.session.domain.InterviewSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuestionServiceTest extends IntegrationTest {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private InterviewManager interviewManager;

    @Autowired
    private InterviewSessionManager interviewSessionManager;

    @Test
    @DisplayName("질문 생성 성공 시 질문이 반환된다")
    void createQuestion() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        // when
        Question question = questionService.create(session.getId());

        // then
        assertThat(question.getId()).isNotNull();
        assertThat(question.getUserId()).isEqualTo(userId);
        assertThat(question.getInterviewId()).isEqualTo(interview.getId());
        assertThat(question.getSessionId()).isEqualTo(session.getId());
        assertThat(question.getContent()).contains("[Mock]");
    }

    @Test
    @DisplayName("존재하지 않는 세션으로 질문 생성 시 예외가 발생한다")
    void createQuestionWithNonExistentSession() {
        // given
        Long nonExistentSessionId = 999L;

        // when & then
        assertThatThrownBy(() -> questionService.create(nonExistentSessionId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.SESSION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("질문 수 초과 시 예외가 발생한다")
    void createQuestionExceedingCount() {
        // given
        Long userId = 1L;
        int questionCount = 5;
        Interview interview = createInterview(userId, questionCount);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        for (int i = 0; i < questionCount; i++) {
            questionService.create(session.getId());
        }

        // when & then
        assertThatThrownBy(() -> questionService.create(session.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.QUESTION_COUNT_EXCEEDED.getMessage());
    }

    private Interview createInterview(Long userId) {
        return createInterview(userId, 5);
    }

    private Interview createInterview(Long userId, int questionCount) {
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.CS, Difficulty.JUNIOR, questionCount, InterviewerPersonality.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of());
        return interviewManager.create(userId, command);
    }
}
