package wlsh.project.intervai.interview.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import wlsh.project.intervai.answer.application.AnswerResultGenerator;
import wlsh.project.intervai.answer.application.dto.AnswerResultDto;
import wlsh.project.intervai.answer.infra.AnswerRepository;
import wlsh.project.intervai.answer.presentation.dto.CreateAnswerResponse;
import wlsh.project.intervai.common.ApiIntegrationTest;
import wlsh.project.intervai.feedback.infra.FeedbackRepository;
import wlsh.project.intervai.interview.infra.InterviewCsSubjectRepository;
import wlsh.project.intervai.interview.infra.InterviewPortfolioLinkRepository;
import wlsh.project.intervai.interview.infra.InterviewRepository;
import wlsh.project.intervai.interview.presentation.dto.CreateInterviewResponse;
import wlsh.project.intervai.interview.presentation.dto.CreateQuestionsResponse;
import wlsh.project.intervai.interview.presentation.dto.CreateSessionResponse;
import wlsh.project.intervai.interview.presentation.dto.NextQuestionResponse;
import wlsh.project.intervai.profile.infra.PortfolioLinkRepository;
import wlsh.project.intervai.profile.infra.ProfileRepository;
import wlsh.project.intervai.profile.infra.ProfileTechStackRepository;
import wlsh.project.intervai.profile.infra.TechStackRepository;
import wlsh.project.intervai.question.application.QuestionGenerator;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.question.infra.QuestionRepository;
import wlsh.project.intervai.session.domain.SessionStatus;
import wlsh.project.intervai.session.infra.InterviewSessionRepository;
import wlsh.project.intervai.user.infra.UserRepository;
import wlsh.project.intervai.user.presentation.dto.LoginResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static wlsh.project.intervai.common.entity.EntityStatus.ACTIVE;

@Import(InterviewSequenceIntegrationTest.SequenceGeneratorTestConfig.class)
class InterviewSequenceIntegrationTest extends ApiIntegrationTest {

    private static final int MAX_SEQUENCE_ROUNDS = 50;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileTechStackRepository profileTechStackRepository;

    @Autowired
    private PortfolioLinkRepository portfolioLinkRepository;

    @Autowired
    private TechStackRepository techStackRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private InterviewCsSubjectRepository interviewCsSubjectRepository;

    @Autowired
    private InterviewPortfolioLinkRepository interviewPortfolioLinkRepository;

    @Autowired
    private InterviewSessionRepository interviewSessionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @AfterEach
    void tearDown() {
        feedbackRepository.deleteAllInBatch();
        answerRepository.deleteAllInBatch();
        questionRepository.deleteAllInBatch();
        interviewSessionRepository.deleteAllInBatch();
        interviewCsSubjectRepository.deleteAllInBatch();
        interviewPortfolioLinkRepository.deleteAllInBatch();
        interviewRepository.deleteAllInBatch();
        portfolioLinkRepository.deleteAllInBatch();
        profileTechStackRepository.deleteAllInBatch();
        profileRepository.deleteAllInBatch();
        techStackRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        flushRedis();
    }

    @Test
    @DisplayName("전체 면접 시퀀스를 끝까지 진행한다")
    void runInterviewSequence() throws Exception {
        LoginResponse login = authScenario();
        CreateInterviewResponse interview = createInterviewSessionQuestionScenario(login);
        List<NextQuestionResponse> askedQuestions = answerQuestionLoopScenario(login, interview);
        finishScenario(login, interview, askedQuestions);
    }

    private LoginResponse authScenario() throws IOException, InterruptedException {
        String nickname = uniqueNickname();
        String password = "pass1234";

        userSteps().signUp(nickname, password);
        return userSteps().login(nickname, password);
    }

    private CreateInterviewResponse createInterviewSessionQuestionScenario(LoginResponse login) throws IOException, InterruptedException {
        CreateInterviewResponse interview = interviewSteps().createInterview(login.accessToken());
        CreateSessionResponse session = sessionSteps().createSession(login.accessToken(), interview.id());
        CreateQuestionsResponse questions = questionSteps().createQuestions(login.accessToken(), interview.id());

        assertThat(session.sessionId()).isNotNull();
        assertThat(questions.questions()).hasSize(5);
        return interview;
    }

    private List<NextQuestionResponse> answerQuestionLoopScenario(LoginResponse login, CreateInterviewResponse interview) throws IOException, InterruptedException {
        List<NextQuestionResponse> askedQuestions = new ArrayList<>();
        List<CreateAnswerResponse> answers = new ArrayList<>();

        for (int round = 1; round <= MAX_SEQUENCE_ROUNDS; round++) {
            NextQuestionResponse current = questionSteps().getCurrentQuestion(login.accessToken(), interview.id());
            askedQuestions.add(current);

            CreateAnswerResponse answer = answerSteps().scenario07AnswerCurrentQuestion(
                    login.accessToken(),
                    interview.id(),
                    current.questionId(),
                    "테스트 답변 " + round
            );
            answers.add(answer);

            if (!current.hasNext()) {
                break;
            }
        }

        assertThat(askedQuestions).hasSizeLessThanOrEqualTo(MAX_SEQUENCE_ROUNDS);
        assertThat(askedQuestions).isNotEmpty();
        assertThat(askedQuestions.getLast().questionType()).isEqualTo(QuestionType.QUESTION);
        assertThat(askedQuestions.getLast().hasNext()).isFalse();
        assertThat(answers).allSatisfy(answer -> assertThat(answer.feedback()).isNotBlank());
        return askedQuestions;
    }

    private void finishScenario(LoginResponse login, CreateInterviewResponse interview, List<NextQuestionResponse> askedQuestions) throws IOException, InterruptedException {
        sessionSteps().finishSession(login.accessToken(), interview.id());

        long mainQuestionCount = askedQuestions.stream()
                .filter(question -> question.questionType() == QuestionType.QUESTION)
                .count();
        long followUpQuestionCount = askedQuestions.stream()
                .filter(question -> question.questionType() == QuestionType.FOLLOW_UP)
                .count();

        assertThat(mainQuestionCount).isEqualTo(5);
        assertThat(followUpQuestionCount).isEqualTo(4);

        var savedSession = interviewSessionRepository.findByInterviewIdAndStatus(interview.id(), ACTIVE)
                .orElseThrow();

        assertThat(savedSession.getSessionStatus()).isEqualTo(SessionStatus.COMPLETED);
        assertThat(savedSession.getCurrentMainQuestionIdx()).isEqualTo(5);
        assertThat(savedSession.getFollowUpCount()).isZero();
        assertThat(savedSession.getCompletedAt()).isNotNull();

        long savedQuestionCount = questionRepository.findAll().stream()
                .filter(question -> question.getInterviewId().equals(interview.id()))
                .count();

        assertThat(savedQuestionCount).isEqualTo(9);
        assertThat(answerRepository.findAll()).hasSize(9);
        assertThat(feedbackRepository.findAll()).hasSize(9);
    }

    private String uniqueNickname() {
        return "u" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }

    @TestConfiguration
    static class SequenceGeneratorTestConfig {

        @Bean
        @Primary
        QuestionGenerator questionGenerator() {
            return interview -> java.util.stream.IntStream.range(0, interview.getQuestionCount())
                    .mapToObj(index -> "[Test] main question " + (index + 1))
                    .toList();
        }

        @Bean
        @Primary
        AnswerResultGenerator answerResultGenerator() {
            return (conversationId, interview, question, answer) -> {
                boolean shouldCreateFollowUp = question.getQuestionType() == QuestionType.QUESTION
                        && question.getQuestionIndex() < interview.getQuestionCount() - 1;

                String followUpQuestion = shouldCreateFollowUp
                        ? "[Test] follow-up for main question " + (question.getQuestionIndex() + 1)
                        : "";

                return new AnswerResultDto(
                        "[Test] feedback for " + question.getContent(),
                        followUpQuestion
                );
            };
        }
    }
}
