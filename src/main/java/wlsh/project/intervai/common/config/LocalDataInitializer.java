package wlsh.project.intervai.common.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.answer.application.AnswerManager;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.common.entity.EntityStatus;
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
import wlsh.project.intervai.profile.domain.UpdateProfileCommand;
import wlsh.project.intervai.question.application.QuestionManager;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.report.application.InterviewReportManager;
import wlsh.project.intervai.report.domain.InterviewReport;
import wlsh.project.intervai.report.domain.ReportQuestion;
import wlsh.project.intervai.session.application.InterviewSessionFinder;
import wlsh.project.intervai.session.application.InterviewSessionManager;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.user.application.UserAuthHandler;
import wlsh.project.intervai.user.domain.User;
import wlsh.project.intervai.user.infra.UserRepository;

@Slf4j
@Component
@Profile("local-seed")
@RequiredArgsConstructor
public class LocalDataInitializer implements ApplicationRunner {

    private static final String LOCAL_NICKNAME = "test";
    private static final String LOCAL_PASSWORD = "test";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAuthHandler userAuthHandler;
    private final ProfileManager profileManager;
    private final InterviewManager interviewManager;
    private final InterviewSessionManager interviewSessionManager;
    private final InterviewSessionFinder interviewSessionFinder;
    private final QuestionManager questionManager;
    private final AnswerManager answerManager;
    private final FeedbackManager feedbackManager;
    private final InterviewReportManager interviewReportManager;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByNicknameAndStatus(LOCAL_NICKNAME, EntityStatus.ACTIVE)) {
            log.info("[LocalDataInitializer] local seed already exists - nickname={}", LOCAL_NICKNAME);
            return;
        }

        User user = userAuthHandler.signUp(LOCAL_NICKNAME, passwordEncoder.encode(LOCAL_PASSWORD));
        profileManager.updateByUserId(user.getId(), new UpdateProfileCommand(
                JobCategory.BACKEND,
                CareerLevel.JUNIOR,
                List.of("Java", "Spring Boot", "JPA", "Redis"),
                List.of("https://github.com/wlsh44/IntervAI")
        ));

        Interview interview = interviewManager.create(user.getId(), new CreateInterviewCommand(
                JobCategory.BACKEND,
                InterviewType.ALL,
                Difficulty.JUNIOR,
                5,
                InterviewerTone.NORMAL,
                List.of(
                        CsSubject.of(CsCategory.NETWORK, List.of("HTTP", "TCP/IP")),
                        CsSubject.of(CsCategory.DATABASE, List.of("INDEX", "TRANSACTION")),
                        CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map", "Queue"))
                ),
                List.of("https://github.com/wlsh44/IntervAI"),
                List.of("Java", "Spring Boot", "JPA", "Redis")
        ));

        InterviewSession session = interviewSessionManager.create(interview.getId(), user.getId());
        List<Question> questions = createQuestions(interview, session);
        List<ReportQuestion> reportQuestions = answerQuestions(user, questions);

        interviewSessionManager.complete(interview.getId());
        InterviewSession completedSession = interviewSessionFinder.find(session.getId());
        interviewReportManager.create(InterviewReport.create(
                interview.getId(),
                interview.getInterviewType(),
                interview.getJobCategory().name(),
                interview.getDifficulty(),
                interview.getQuestionCount(),
                completedSession.getCompletedAt(),
                84,
                "전반적으로 핵심 개념을 잘 설명했습니다. 답변에 구체적인 근거와 트레이드오프를 더하면 완성도가 높아집니다.",
                reportQuestions
        ));

        log.info("[LocalDataInitializer] local seed created - nickname={}, password={}",
                LOCAL_NICKNAME, LOCAL_PASSWORD);
    }

    private List<Question> createQuestions(Interview interview, InterviewSession session) {
        return List.of(
                questionManager.create(interview.getId(), session.getId(),
                        "HTTP와 HTTPS의 차이를 설명해주세요.", QuestionType.QUESTION, 0),
                questionManager.create(interview.getId(), session.getId(),
                        "데이터베이스 인덱스가 조회 성능을 높이는 원리를 설명해주세요.", QuestionType.QUESTION, 1),
                questionManager.create(interview.getId(), session.getId(),
                        "HashMap의 평균 조회 시간 복잡도와 충돌 처리 방식을 설명해주세요.", QuestionType.QUESTION, 2),
                questionManager.create(interview.getId(), session.getId(),
                        "Spring에서 트랜잭션 전파 옵션이 필요한 상황을 예로 들어 설명해주세요.", QuestionType.QUESTION, 3),
                questionManager.create(interview.getId(), session.getId(),
                        "지원한 프로젝트에서 성능 병목을 찾아 개선한 경험을 설명해주세요.", QuestionType.QUESTION, 4)
        );
    }

    private List<ReportQuestion> answerQuestions(User user, List<Question> questions) {
        return questions.stream()
                .map(question -> answerQuestion(user, question))
                .toList();
    }

    private ReportQuestion answerQuestion(User user, Question question) {
        String answerContent = "핵심 개념과 실제 프로젝트 경험을 바탕으로 답변한 로컬 시드 데이터입니다.";
        String feedbackContent = "기본 개념은 잘 설명했습니다. 구체적인 수치나 장애 사례를 함께 말하면 더 좋습니다.";
        int score = 80 + question.getQuestionIndex();

        Answer answer = answerManager.create(user.getId(), question.getId(), answerContent);
        feedbackManager.create(answer.getId(), feedbackContent, score);

        return new ReportQuestion(
                question.getId(),
                question.getQuestionIndex(),
                question.getContent(),
                answerContent,
                feedbackContent,
                score,
                List.of("로컬", "시드", "면접"),
                List.of()
        );
    }
}
