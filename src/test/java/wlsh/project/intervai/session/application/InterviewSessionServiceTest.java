package wlsh.project.intervai.session.application;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.answer.infra.AnswerEntity;
import wlsh.project.intervai.answer.infra.AnswerRepository;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.application.InterviewManager;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;
import wlsh.project.intervai.question.application.QuestionManager;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.domain.SessionHistory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterviewSessionServiceTest extends IntegrationTest {

    @Autowired
    private InterviewSessionService interviewSessionService;

    @Autowired
    private InterviewSessionManager interviewSessionManager;

    @Autowired
    private InterviewManager interviewManager;

    @Autowired
    private QuestionManager questionManager;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("모든 질문에 답변이 있으면 answerId가 채워진 히스토리를 반환한다")
    void findSessionHistory_allAnswered() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        Question q1 = questionManager.create(interview.getId(), session.getId(), "질문1", QuestionType.QUESTION, 0);
        Question q2 = questionManager.create(interview.getId(), session.getId(), "질문2", QuestionType.QUESTION, 1);
        saveAnswer(userId, interview.getId(), session.getId(), q1.getId(), "답변1");
        saveAnswer(userId, interview.getId(), session.getId(), q2.getId(), "답변2");

        // when
        List<SessionHistory> result = interviewSessionService.findSessionHistory(userId, interview.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(h -> h.answerId() != null);
        assertThat(result).allMatch(h -> h.answerContent() != null);
    }

    @Test
    @DisplayName("답변 없는 질문은 answerId와 answerContent가 null이다")
    void findSessionHistory_unansweredQuestion_answerIdIsNull() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        Question q1 = questionManager.create(interview.getId(), session.getId(), "답변한 질문", QuestionType.QUESTION, 0);
        questionManager.create(interview.getId(), session.getId(), "답변 안 한 질문", QuestionType.QUESTION, 1);
        saveAnswer(userId, interview.getId(), session.getId(), q1.getId(), "답변1");

        // when
        List<SessionHistory> result = interviewSessionService.findSessionHistory(userId, interview.getId());

        // then
        assertThat(result).hasSize(2);

        SessionHistory answered = result.stream()
                .filter(h -> "답변한 질문".equals(h.questionContent()))
                .findFirst().orElseThrow();
        assertThat(answered.answerId()).isNotNull();
        assertThat(answered.answerContent()).isEqualTo("답변1");

        SessionHistory unanswered = result.stream()
                .filter(h -> "답변 안 한 질문".equals(h.questionContent()))
                .findFirst().orElseThrow();
        assertThat(unanswered.answerId()).isNull();
        assertThat(unanswered.answerContent()).isNull();
    }

    @Test
    @DisplayName("질문이 없으면 빈 리스트를 반환한다")
    void findSessionHistory_noQuestions_returnsEmptyList() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId);
        interviewSessionManager.create(interview.getId(), userId);

        // when
        List<SessionHistory> result = interviewSessionService.findSessionHistory(userId, interview.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("히스토리는 질문 생성 순서대로 반환된다")
    void findSessionHistory_returnsQuestionsInCreationOrder() {
        Long userId = 1L;
        Interview interview = createInterview(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        Question q1 = questionManager.create(interview.getId(), session.getId(), "질문1", QuestionType.QUESTION, 0);
        Question q2 = questionManager.create(interview.getId(), session.getId(), "질문2", QuestionType.QUESTION, 1);
        Question followUp1 = questionManager.createFollowUp(interview.getId(), session.getId(), q1.getId(), "꼬리질문1")
                .orElseThrow();
        questionManager.createFollowUp(interview.getId(), session.getId(), followUp1.getId(), "꼬리질문2")
                .orElseThrow();

        List<SessionHistory> result = interviewSessionService.findSessionHistory(userId, interview.getId());

        assertThat(result)
                .extracting(SessionHistory::questionContent)
                .containsExactly("질문1", "질문2", "꼬리질문1", "꼬리질문2");
    }

    @Test
    @DisplayName("부모 정보가 없어도 히스토리는 생성 순서를 유지한다")
    void findSessionHistory_keepsCreationOrderWhenParentIsMissing() {
        Long userId = 1L;
        Interview interview = createInterview(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        Question q1 = questionManager.create(interview.getId(), session.getId(), "질문1", QuestionType.QUESTION, 0);
        Question q2 = questionManager.create(interview.getId(), session.getId(), "질문2", QuestionType.QUESTION, 1);
        Question followUp1 = questionManager.createFollowUp(interview.getId(), session.getId(), null, "꼬리질문1")
                .orElseThrow();
        Question followUp2 = questionManager.createFollowUp(interview.getId(), session.getId(), null, "꼬리질문2")
                .orElseThrow();

        saveAnswer(userId, interview.getId(), session.getId(), q1.getId(), "답변1");
        saveAnswer(userId, interview.getId(), session.getId(), followUp1.getId(), "답변1-1");
        saveAnswer(userId, interview.getId(), session.getId(), followUp2.getId(), "답변1-2");
        saveAnswer(userId, interview.getId(), session.getId(), q2.getId(), "답변2");

        List<SessionHistory> result = interviewSessionService.findSessionHistory(userId, interview.getId());

        assertThat(result)
                .extracting(SessionHistory::questionContent)
                .containsExactly("질문1", "질문2", "꼬리질문1", "꼬리질문2");
    }

    @Test
    @DisplayName("답변된 부모를 가진 미답변 꼬리 질문도 히스토리에서 누락되지 않는다")
    void findSessionHistory_unansweredFollowUpWithAnsweredParent_isNotDropped() {
        Long userId = 1L;
        Interview interview = createInterview(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        Question q1 = questionManager.create(interview.getId(), session.getId(), "질문1", QuestionType.QUESTION, 0);
        Question q2 = questionManager.create(interview.getId(), session.getId(), "질문2", QuestionType.QUESTION, 1);
        Question followUp = questionManager.createFollowUp(interview.getId(), session.getId(), q1.getId(), "꼬리질문1")
                .orElseThrow();

        saveAnswer(userId, interview.getId(), session.getId(), q1.getId(), "답변1");

        List<SessionHistory> result = interviewSessionService.findSessionHistory(userId, interview.getId());

        assertThat(result)
                .extracting(SessionHistory::questionContent)
                .containsExactly("질문1", "질문2", "꼬리질문1");

        SessionHistory unansweredFollowUp = result.stream()
                .filter(history -> history.questionId().equals(followUp.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(unansweredFollowUp.answerId()).isNull();
    }

    @Test
    @DisplayName("타인의 면접 히스토리 조회 시 INTERVIEW_ACCESS_DENIED 예외가 발생한다")
    void findSessionHistory_otherUserInterview_throwsAccessDenied() {
        // given
        Long ownerId = 1L;
        Long otherUserId = 2L;
        Interview interview = createInterview(ownerId);

        // when & then
        assertThatThrownBy(() -> interviewSessionService.findSessionHistory(otherUserId, interview.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INTERVIEW_ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 면접 조회 시 INTERVIEW_NOT_FOUND 예외가 발생한다")
    void findSessionHistory_interviewNotFound_throwsException() {
        // given
        Long userId = 1L;
        Long nonExistentInterviewId = 999L;

        // when & then
        assertThatThrownBy(() -> interviewSessionService.findSessionHistory(userId, nonExistentInterviewId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INTERVIEW_NOT_FOUND.getMessage());
    }

    private Interview createInterview(Long userId) {
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.CS, Difficulty.JUNIOR, 5, InterviewerTone.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of(), List.of());
        return interviewManager.create(userId, command);
    }

    private void saveAnswer(Long userId, Long interviewId, Long sessionId, Long questionId, String content) {
        Answer answer = Answer.create(userId, interviewId, sessionId, questionId, content);
        answerRepository.save(AnswerEntity.from(answer));
        entityManager.flush();
    }
}
