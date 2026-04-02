package wlsh.project.intervai.question.application;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.InterviewerTone;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.interview.application.InterviewFinder;
import wlsh.project.intervai.interview.application.InterviewManager;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerPersonality;
import wlsh.project.intervai.session.application.InterviewSessionFinder;
import wlsh.project.intervai.session.application.InterviewSessionManager;
import wlsh.project.intervai.session.domain.InterviewSession;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuestionValidatorTest extends IntegrationTest {

    @Autowired
    private QuestionValidator questionValidator;

    @Autowired
    private QuestionManager questionManager;

    @Autowired
    private InterviewManager interviewManager;

    @Autowired
    private InterviewSessionManager interviewSessionManager;

    @Autowired
    private InterviewSessionFinder interviewSessionFinder;

    @Autowired
    private InterviewFinder interviewFinder;

    @Test
    @DisplayName("진행 중인 세션이고 질문 수가 초과하지 않으면 검증에 성공한다")
    void validateSuccess() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId, 5);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        InterviewSession foundSession = interviewSessionFinder.find(session.getId());
        Interview foundInterview = interviewFinder.find(interview.getId());

        // when & then
        assertThatCode(() -> questionValidator.validate(foundSession, foundInterview))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("질문 수가 초과하면 예외가 발생한다")
    void validateWithExceededQuestionCount() {
        // given
        Long userId = 1L;
        int questionCount = 5;
        Interview interview = createInterview(userId, questionCount);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        for (int i = 0; i < questionCount; i++) {
            questionManager.create(userId, interview.getId(), session.getId(), "질문 " + (i + 1));
        }

        InterviewSession foundSession = interviewSessionFinder.find(session.getId());
        Interview foundInterview = interviewFinder.find(interview.getId());

        // when & then
        assertThatThrownBy(() -> questionValidator.validate(foundSession, foundInterview))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.QUESTION_COUNT_EXCEEDED.getMessage());
    }

    private Interview createInterview(Long userId, int questionCount) {
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.CS, Difficulty.JUNIOR, questionCount, InterviewerTone.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of());
        return interviewManager.create(userId, command);
    }
}
