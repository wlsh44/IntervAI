package wlsh.project.intervai.question.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.domain.JobCategory;
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
import wlsh.project.intervai.interview.domain.NextQuestionResult;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.session.application.InterviewSessionManager;
import wlsh.project.intervai.session.domain.InterviewSession;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuestionFinderTest extends IntegrationTest {

    @Autowired
    private QuestionFinder questionFinder;

    @Autowired
    private QuestionManager questionManager;

    @Autowired
    private InterviewManager interviewManager;

    @Autowired
    private InterviewSessionManager interviewSessionManager;

    @Test
    @DisplayName("followUpCount가 0이면 현재 인덱스의 본 질문을 반환한다")
    void findCurrentReturnsMainQuestion() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId, 5);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);
        Question question = questionManager.create(interview.getId(), session.getId(), "본 질문 내용", QuestionType.QUESTION, 0);

        // when
        NextQuestionResult result = questionFinder.findCurrent(session.getId(), 0, 0, 5, interview.getMaxFollowUpCount());

        // then
        assertThat(result.question().getId()).isEqualTo(question.getId());
        assertThat(result.question().getQuestionType()).isEqualTo(QuestionType.QUESTION);
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("followUpCount가 0보다 크면 가장 최근 꼬리 질문을 반환한다")
    void findCurrentReturnsFollowUpQuestion() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId, 5);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);
        questionManager.create(interview.getId(), session.getId(), "본 질문 내용", QuestionType.QUESTION, 0);
        questionManager.createFollowUp(interview.getId(), session.getId(), null, "꼬리 질문 내용");

        // when
        NextQuestionResult result = questionFinder.findCurrent(session.getId(), 0, 1, 5, interview.getMaxFollowUpCount());

        // then
        assertThat(result.question().getQuestionType()).isEqualTo(QuestionType.FOLLOW_UP);
        assertThat(result.question().getContent()).isEqualTo("꼬리 질문 내용");
    }

    @Test
    @DisplayName("꼬리 질문이 여러 개일 때 가장 최근 꼬리 질문을 반환한다")
    void findCurrentReturnsLatestFollowUp() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId, 5);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);
        questionManager.create(interview.getId(), session.getId(), "본 질문", QuestionType.QUESTION, 0);
        questionManager.createFollowUp(interview.getId(), session.getId(), null, "꼬리 질문 1");
        questionManager.createFollowUp(interview.getId(), session.getId(), null, "꼬리 질문 2");

        // when
        NextQuestionResult result = questionFinder.findCurrent(session.getId(), 0, 2, 5, interview.getMaxFollowUpCount());

        // then
        assertThat(result.question().getContent()).isEqualTo("꼬리 질문 2");
    }

    @Test
    @DisplayName("마지막 본 질문이어도 꼬리 질문 여지가 있으면 hasNext가 true이다")
    void findCurrentLastMainQuestionStillHasNext() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId, 3);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);
        questionManager.create(interview.getId(), session.getId(), "마지막 질문", QuestionType.QUESTION, 2);

        // when
        NextQuestionResult result = questionFinder.findCurrent(session.getId(), 2, 0, 3, interview.getMaxFollowUpCount());

        // then
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("마지막 질문의 마지막 꼬리 질문이면 hasNext가 false이다")
    void findCurrentLastFollowUpHasNoNext() {
        Long userId = 1L;
        Interview interview = createInterview(userId, 3);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);
        Question mainQuestion = questionManager.create(interview.getId(), session.getId(), "마지막 질문", QuestionType.QUESTION, 2);
        Question followUp1 = questionManager.createFollowUp(interview.getId(), session.getId(), mainQuestion.getId(), "꼬리 질문 1")
                .orElseThrow();
        Question followUp2 = questionManager.createFollowUp(interview.getId(), session.getId(), followUp1.getId(), "꼬리 질문 2")
                .orElseThrow();
        questionManager.createFollowUp(interview.getId(), session.getId(), followUp2.getId(), "꼬리 질문 3")
                .orElseThrow();

        NextQuestionResult result = questionFinder.findCurrent(
                session.getId(),
                2,
                interview.getMaxFollowUpCount(),
                3,
                interview.getMaxFollowUpCount()
        );

        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 본 질문 조회 시 예외가 발생한다")
    void findCurrentMainQuestionNotFound() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId, 5);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        // when & then
        assertThatThrownBy(() -> questionFinder.findCurrent(session.getId(), 0, 0, 5, interview.getMaxFollowUpCount()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.QUESTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("followUpCount가 0보다 크지만 꼬리 질문이 없으면 예외가 발생한다")
    void findCurrentFollowUpNotFound() {
        // given
        Long userId = 1L;
        Interview interview = createInterview(userId, 5);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);

        // when & then
        assertThatThrownBy(() -> questionFinder.findCurrent(session.getId(), 0, 1, 5, interview.getMaxFollowUpCount()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.QUESTION_NOT_FOUND.getMessage());
    }

    private Interview createInterview(Long userId, int questionCount) {
        CreateInterviewCommand command = new CreateInterviewCommand(
                JobCategory.BACKEND, InterviewType.CS, Difficulty.JUNIOR, questionCount, InterviewerTone.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of(), List.of());
        return interviewManager.create(userId, command);
    }
}
