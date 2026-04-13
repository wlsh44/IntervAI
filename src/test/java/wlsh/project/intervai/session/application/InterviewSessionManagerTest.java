package wlsh.project.intervai.session.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.interview.application.InterviewManager;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;
import wlsh.project.intervai.session.domain.InterviewSession;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewSessionManagerTest extends IntegrationTest {

    @Autowired
    private InterviewSessionManager interviewSessionManager;

    @Autowired
    private InterviewSessionFinder interviewSessionFinder;

    @Autowired
    private InterviewManager interviewManager;

    @Test
    @DisplayName("다음 질문으로 이동하면 currentMainQuestionIdx가 1 증가하고 followUpCount가 0으로 초기화된다")
    void advanceToNext() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);
        interviewSessionManager.addFollowUpCount(session.getId());

        // when
        interviewSessionManager.advanceToNext(session.getId());

        // then
        InterviewSession updated = interviewSessionFinder.find(session.getId());
        assertThat(updated.getCurrentMainQuestionIdx()).isEqualTo(1);
        assertThat(updated.getFollowUpCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("꼬리 질문을 추가하면 followUpCount가 1 증가한다")
    void addFollowUpCount() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        // when
        interviewSessionManager.addFollowUpCount(session.getId());

        // then
        InterviewSession updated = interviewSessionFinder.find(session.getId());
        assertThat(updated.getFollowUpCount()).isEqualTo(1);
        assertThat(updated.getCurrentMainQuestionIdx()).isEqualTo(0);
    }

    @Test
    @DisplayName("꼬리 질문을 여러 번 추가하면 followUpCount가 누적된다")
    void addMultipleFollowUps() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        // when
        interviewSessionManager.addFollowUpCount(session.getId());
        interviewSessionManager.addFollowUpCount(session.getId());
        interviewSessionManager.addFollowUpCount(session.getId());

        // then
        InterviewSession updated = interviewSessionFinder.find(session.getId());
        assertThat(updated.getFollowUpCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("다음 질문으로 이동 후 followUpCount는 초기화되어 꼬리 질문을 다시 쌓을 수 있다")
    void advanceToNextResetsFollowUpCount() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);
        interviewSessionManager.addFollowUpCount(session.getId());
        interviewSessionManager.addFollowUpCount(session.getId());
        interviewSessionManager.advanceToNext(session.getId());

        // when
        interviewSessionManager.addFollowUpCount(session.getId());

        // then
        InterviewSession updated = interviewSessionFinder.find(session.getId());
        assertThat(updated.getCurrentMainQuestionIdx()).isEqualTo(1);
        assertThat(updated.getFollowUpCount()).isEqualTo(1);
    }

    private Interview createInterview(Long userId) {
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.CS, Difficulty.JUNIOR, 5, InterviewerTone.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of(), List.of());
        return interviewManager.create(userId, command);
    }
}
