package wlsh.project.intervai.session.application;

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
import wlsh.project.intervai.interview.domain.InterviewerTone;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.domain.InterviewSessionStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterviewSessionServiceTest extends IntegrationTest {

    @Autowired
    private InterviewSessionService interviewSessionService;

    @Autowired
    private InterviewManager interviewManager;

    @Test
    @DisplayName("면접 세션을 생성하면 IN_PROGRESS 상태의 세션이 반환된다")
    void createSession() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId);

        // when
        InterviewSession session = interviewSessionService.create(userId, interview.getId());

        // then
        assertThat(session.getId()).isNotNull();
        assertThat(session.getInterviewId()).isEqualTo(interview.getId());
        assertThat(session.getUserId()).isEqualTo(userId);
        assertThat(session.getSessionStatus()).isEqualTo(InterviewSessionStatus.IN_PROGRESS);
        assertThat(session.getCurrentQuestionCount()).isZero();
        assertThat(session.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 면접으로 세션 생성 시 예외가 발생한다")
    void createSessionWithNonExistentInterview() {
        // given
        Long userId = 1L;
        Long nonExistentInterviewId = 999L;

        // when & then
        assertThatThrownBy(() -> interviewSessionService.create(userId, nonExistentInterviewId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INTERVIEW_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("타인의 면접으로 세션 생성 시 예외가 발생한다")
    void createSessionWithOtherUserInterview() {
        // given
        Long ownerId = 1L;
        Long otherUserId = 2L;
        Interview interview = createInterview(ownerId);

        // when & then
        assertThatThrownBy(() -> interviewSessionService.create(otherUserId, interview.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INTERVIEW_ACCESS_DENIED.getMessage());
    }

    private Interview createInterview(Long userId) {
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.CS, Difficulty.JUNIOR, 5, InterviewerTone.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of());
        return interviewManager.create(userId, command);
    }
}
