package wlsh.project.intervai.report.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.answer.infra.AnswerEntity;
import wlsh.project.intervai.answer.infra.AnswerRepository;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.feedback.application.FeedbackManager;
import wlsh.project.intervai.interview.application.InterviewManager;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;
import wlsh.project.intervai.profile.application.ProfileManager;
import wlsh.project.intervai.profile.domain.CareerLevel;
import wlsh.project.intervai.profile.domain.CreateProfileCommand;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.question.application.QuestionManager;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.report.domain.InterviewReport;
import wlsh.project.intervai.report.domain.ReportQuestion;
import wlsh.project.intervai.session.application.InterviewSessionManager;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.domain.SessionStatus;

class InterviewReportServiceTest extends IntegrationTest {

    @Autowired
    private InterviewReportService interviewReportService;

    @Autowired
    private InterviewReportManager interviewReportManager;

    @Autowired
    private InterviewManager interviewManager;

    @Autowired
    private InterviewSessionManager interviewSessionManager;

    @Autowired
    private ProfileManager profileManager;

    @Autowired
    private QuestionManager questionManager;

    @Autowired
    private FeedbackManager feedbackManager;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("generateReport는 종료된 세션의 질문/점수/키워드를 조합해 리포트를 저장한다")
    void generateReport_createsReport() {
        Long userId = 1L;
        Interview interview = createInterview(userId);
        createProfile(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);
        Question mainQuestion = questionManager.create(interview.getId(), session.getId(), "메인 질문", QuestionType.QUESTION, 0);
        questionManager.createFollowUp(interview.getId(), session.getId(), mainQuestion.getId(), "꼬리 질문");
        Answer answer = saveAnswer(userId, interview.getId(), session.getId(), mainQuestion.getId(), "실제 답변");
        feedbackManager.create(answer.getId(), "실제 피드백", 87);
        interviewSessionManager.complete(interview.getId());

        interviewReportService.generateReport(interview.getId());

        InterviewReport result = interviewReportService.getReport(userId, interview.getId());

        assertThat(result.jobCategory()).isEqualTo(JobCategory.BACKEND.name());
        assertThat(result.totalScore()).isEqualTo(82);
        assertThat(result.questions()).hasSize(1);
        assertThat(result.questions().getFirst().score()).isEqualTo(87);
        assertThat(result.questions().getFirst().keywords()).isEmpty();
    }

    @Test
    @DisplayName("getReport는 저장된 키워드와 실제 세션 히스토리를 조합해 최종 리포트를 반환한다")
    void getReport_assemblesQuestionsFromHistory() {
        Long userId = 1L;
        Interview interview = createInterview(userId);
        createProfile(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);
        Question mainQuestion = questionManager.create(interview.getId(), session.getId(), "메인 질문", QuestionType.QUESTION, 0);
        questionManager.createFollowUp(interview.getId(), session.getId(), mainQuestion.getId(), "꼬리 질문");
        Answer answer = saveAnswer(userId, interview.getId(), session.getId(), mainQuestion.getId(), "실제 답변");
        feedbackManager.create(answer.getId(), "실제 피드백", 87);
        interviewSessionManager.complete(interview.getId());

        InterviewSession completedSession = refreshSession(session.getId());
        interviewReportManager.create(InterviewReport.create(
                interview.getId(),
                interview.getInterviewType(),
                JobCategory.BACKEND.name(),
                interview.getDifficulty(),
                interview.getQuestionCount(),
                completedSession.getCompletedAt(),
                92,
                "저장된 총평",
                List.of(new ReportQuestion(
                        mainQuestion.getId(),
                        0,
                        "저장용 질문",
                        null,
                        null,
                        null,
                        List.of("SPRING", "JPA", "ORM"),
                        List.of()
                ))
        ));

        InterviewReport result = interviewReportService.getReport(userId, interview.getId());

        assertThat(result.totalScore()).isEqualTo(92);
        assertThat(result.overallComment()).isEqualTo("저장된 총평");
        assertThat(result.questions()).hasSize(1);

        ReportQuestion reportQuestion = result.questions().getFirst();
        assertThat(reportQuestion.questionId()).isEqualTo(mainQuestion.getId());
        assertThat(reportQuestion.questionContent()).isEqualTo("메인 질문");
        assertThat(reportQuestion.answerContent()).isEqualTo("실제 답변");
        assertThat(reportQuestion.feedbackContent()).isEqualTo("실제 피드백");
        assertThat(reportQuestion.score()).isEqualTo(87);
        assertThat(reportQuestion.keywords()).containsExactly("SPRING", "JPA", "ORM");
    }

    @Test
    @DisplayName("generateReport는 이미 리포트가 존재하면 REPORT_ALREADY_EXISTS 예외를 던진다")
    void generateReport_whenAlreadyExists_throwsException() {
        Long userId = 1L;
        Interview interview = createInterview(userId);
        createProfile(userId);
        InterviewSession session = interviewSessionManager.create(interview.getId(), userId);
        interviewSessionManager.complete(interview.getId());
        InterviewSession completedSession = refreshSession(session.getId());

        interviewReportManager.create(InterviewReport.create(
                interview.getId(),
                interview.getInterviewType(),
                JobCategory.BACKEND.name(),
                interview.getDifficulty(),
                interview.getQuestionCount(),
                completedSession.getCompletedAt(),
                80,
                "기존 리포트",
                List.of()
        ));

        assertThatThrownBy(() -> interviewReportService.generateReport(interview.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.REPORT_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("generateReport는 세션이 미완료 상태면 SESSION_NOT_COMPLETED 예외를 던진다")
    void generateReport_whenSessionNotCompleted_throwsException() {
        Long userId = 1L;
        Interview interview = createInterview(userId);
        createProfile(userId);
        interviewSessionManager.create(interview.getId(), userId);

        assertThatThrownBy(() -> interviewReportService.generateReport(interview.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.SESSION_NOT_COMPLETED.getMessage());
    }

    @Test
    @DisplayName("getReport는 리포트가 없으면 REPORT_NOT_FOUND 예외를 던진다")
    void getReport_whenReportMissing_throwsException() {
        Long userId = 1L;
        Interview interview = createInterview(userId);
        createProfile(userId);
        interviewSessionManager.create(interview.getId(), userId);
        interviewSessionManager.complete(interview.getId());

        assertThatThrownBy(() -> interviewReportService.getReport(userId, interview.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.REPORT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getReport는 세션이 미완료 상태면 SESSION_NOT_COMPLETED 예외를 던진다")
    void getReport_whenSessionNotCompleted_throwsException() {
        Long userId = 1L;
        Interview interview = createInterview(userId);
        createProfile(userId);
        interviewSessionManager.create(interview.getId(), userId);

        assertThatThrownBy(() -> interviewReportService.getReport(userId, interview.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.SESSION_NOT_COMPLETED.getMessage());
    }

    private Interview createInterview(Long userId) {
        CreateInterviewCommand command = new CreateInterviewCommand(
                JobCategory.BACKEND,
                InterviewType.CS,
                Difficulty.JUNIOR,
                2,
                InterviewerTone.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))),
                List.of(),
                List.of()
        );
        return interviewManager.create(userId, command);
    }

    private void createProfile(Long userId) {
        profileManager.create(userId, new CreateProfileCommand(
                JobCategory.BACKEND,
                CareerLevel.JUNIOR,
                List.of("Spring"),
                List.of()
        ));
    }

    private Answer saveAnswer(Long userId, Long interviewId, Long sessionId, Long questionId, String content) {
        Answer answer = Answer.create(userId, interviewId, sessionId, questionId, content);
        AnswerEntity saved = answerRepository.save(AnswerEntity.from(answer));
        entityManager.flush();
        return saved.toDomain();
    }

    private InterviewSession refreshSession(Long sessionId) {
        entityManager.flush();
        entityManager.clear();
        wlsh.project.intervai.session.infra.InterviewSessionEntity entity =
                entityManager.find(wlsh.project.intervai.session.infra.InterviewSessionEntity.class, sessionId);
        assertThat(entity.getSessionStatus()).isEqualTo(SessionStatus.COMPLETED);
        return entity.toDomain();
    }
}
