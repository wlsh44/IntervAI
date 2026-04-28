package wlsh.project.intervai.question.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.answer.infra.AnswerEntity;
import wlsh.project.intervai.answer.infra.AnswerRepository;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.common.entity.EntityStatus;
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
import wlsh.project.intervai.session.application.InterviewSessionManager;
import wlsh.project.intervai.session.application.dto.SessionHistoryDto;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.infra.InterviewSessionRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionRepositoryTest extends IntegrationTest {

    @Autowired
    private QuestionManager questionManager;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private InterviewManager interviewManager;

    @Autowired
    private InterviewSessionManager interviewSessionManager;
    @Autowired
    private InterviewSessionRepository interviewSessionRepository;

    @Test
    @DisplayName("모든 질문에 답변이 있으면 answerId가 채워진다")
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
        List<SessionHistoryDto> result = interviewSessionRepository.findSessionHistoryByInterviewId(
                interview.getId(), EntityStatus.ACTIVE);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(dto -> dto.getAnswerId() != null);
        assertThat(result).allMatch(dto -> dto.getAnswerContent() != null);
        assertThat(result).allMatch(dto -> dto.getParentQuestionId() == null);
    }

    @Test
    @DisplayName("답변이 없는 질문은 answerId가 null이다")
    void findSessionHistory_unansweredQuestion_answerIdIsNull() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        Question q1 = questionManager.create(interview.getId(), session.getId(), "답변한 질문", QuestionType.QUESTION, 0);
        questionManager.create(interview.getId(), session.getId(), "답변 안 한 질문", QuestionType.QUESTION, 1);

        saveAnswer(userId, interview.getId(), session.getId(), q1.getId(), "답변1");

        // when
        List<SessionHistoryDto> result = interviewSessionRepository.findSessionHistoryByInterviewId(
                interview.getId(), EntityStatus.ACTIVE);

        // then
        assertThat(result).hasSize(2);

        SessionHistoryDto answered = result.stream()
                .filter(dto -> dto.getQuestionContent().equals("답변한 질문"))
                .findFirst().orElseThrow();
        assertThat(answered.getAnswerId()).isNotNull();
        assertThat(answered.getAnswerContent()).isEqualTo("답변1");

        SessionHistoryDto unanswered = result.stream()
                .filter(dto -> dto.getQuestionContent().equals("답변 안 한 질문"))
                .findFirst().orElseThrow();
        assertThat(unanswered.getAnswerId()).isNull();
        assertThat(unanswered.getAnswerContent()).isNull();
    }

    @Test
    @DisplayName("다른 면접의 질문은 조회되지 않는다")
    void findSessionHistory_onlyReturnsQuestionsForGivenInterview() {
        // given
        Long userId = 1L;
        Interview interview1 = createInterview(userId);
        Interview interview2 = createInterview(userId);
        InterviewSession session1 = interviewSessionManager.create(interview1.getId(), userId);
        InterviewSession session2 = interviewSessionManager.create(interview2.getId(), userId);

        questionManager.create(interview1.getId(), session1.getId(), "면접1 질문", QuestionType.QUESTION, 0);
        questionManager.create(interview2.getId(), session2.getId(), "면접2 질문", QuestionType.QUESTION, 0);

        // when
        List<SessionHistoryDto> result = interviewSessionRepository.findSessionHistoryByInterviewId(
                interview1.getId(), EntityStatus.ACTIVE);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getQuestionContent()).isEqualTo("면접1 질문");
    }

    @Test
    @DisplayName("꼬리 질문은 부모 questionId를 함께 조회한다")
    void findSessionHistory_followUpIncludesParentQuestionId() {
        Long userId = 1L;
        Interview interview = createInterview(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        Question main = questionManager.create(interview.getId(), session.getId(), "질문1", QuestionType.QUESTION, 0);
        Question followUp = questionManager.createFollowUp(interview.getId(), session.getId(), main.getId(), "꼬리질문1")
                .orElseThrow();

        List<SessionHistoryDto> result = interviewSessionRepository.findSessionHistoryByInterviewId(
                interview.getId(), EntityStatus.ACTIVE);

        SessionHistoryDto followUpHistory = result.stream()
                .filter(dto -> dto.getQuestionId().equals(followUp.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(followUpHistory.getParentQuestionId()).isEqualTo(main.getId());
    }

    private Interview createInterview(Long userId) {
        CreateInterviewCommand command = new CreateInterviewCommand(
                JobCategory.BACKEND, InterviewType.CS, Difficulty.JUNIOR, 5, InterviewerTone.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of(), List.of());
        return interviewManager.create(userId, command);
    }

    private void saveAnswer(Long userId, Long interviewId, Long sessionId, Long questionId, String content) {
        Answer answer = Answer.create(userId, interviewId, sessionId, questionId, content);
        answerRepository.save(AnswerEntity.from(answer));
    }
}
