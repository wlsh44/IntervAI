package wlsh.project.intervai.interview.application;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.CreateInterviewSessionCommand;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewSession;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerPersonality;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterviewSessionServiceTest extends IntegrationTest {

    @Autowired
    private InterviewSessionService interviewSessionService;

    @Test
    @DisplayName("CS 면접 세션을 생성하면 세션 정보가 반환된다")
    void createCsSession() {
        // given
        Long userId = 1L;
        List<CsSubject> csSubjects = List.of(
                CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map", "List")),
                CsSubject.of(CsCategory.ALGORITHM, List.of("정렬")));
        CreateInterviewSessionCommand command = new CreateInterviewSessionCommand(
                InterviewType.CS, Difficulty.JUNIOR, 7, InterviewerPersonality.FRIENDLY,
                csSubjects, List.of());

        // when
        InterviewSession session = interviewSessionService.create(userId, command);

        // then
        assertThat(session.getId()).isNotNull();
        assertThat(session.getUserId()).isEqualTo(userId);
        assertThat(session.getInterviewType()).isEqualTo(InterviewType.CS);
        assertThat(session.getDifficulty()).isEqualTo(Difficulty.JUNIOR);
        assertThat(session.getQuestionCount()).isEqualTo(7);
        assertThat(session.getInterviewerPersonality()).isEqualTo(InterviewerPersonality.FRIENDLY);
        assertThat(session.getCsSubjects()).hasSize(2);
        assertThat(session.getPortfolioLinks()).isEmpty();
    }

    @Test
    @DisplayName("포트폴리오 면접 세션을 생성하면 세션 정보가 반환된다")
    void createPortfolioSession() {
        // given
        Long userId = 1L;
        CreateInterviewSessionCommand command = new CreateInterviewSessionCommand(
                InterviewType.PORTFOLIO, Difficulty.SENIOR, 5, InterviewerPersonality.AGGRESSIVE,
                List.of(), List.of("https://github.com/user/project"));

        // when
        InterviewSession session = interviewSessionService.create(userId, command);

        // then
        assertThat(session.getId()).isNotNull();
        assertThat(session.getInterviewType()).isEqualTo(InterviewType.PORTFOLIO);
        assertThat(session.getDifficulty()).isEqualTo(Difficulty.SENIOR);
        assertThat(session.getQuestionCount()).isEqualTo(5);
        assertThat(session.getInterviewerPersonality()).isEqualTo(InterviewerPersonality.AGGRESSIVE);
        assertThat(session.getCsSubjects()).isEmpty();
        assertThat(session.getPortfolioLinks()).containsExactly("https://github.com/user/project");
    }

    @Test
    @DisplayName("전체 면접 세션을 생성하면 세션 정보가 반환된다")
    void createAllSession() {
        // given
        Long userId = 1L;
        List<CsSubject> csSubjects = List.of(
                CsSubject.of(CsCategory.NETWORK, List.of("http/https", "tcp/udp")));
        CreateInterviewSessionCommand command = new CreateInterviewSessionCommand(
                InterviewType.ALL, Difficulty.ENTRY, 10, InterviewerPersonality.NORMAL,
                csSubjects, List.of("https://github.com/user/project"));

        // when
        InterviewSession session = interviewSessionService.create(userId, command);

        // then
        assertThat(session.getId()).isNotNull();
        assertThat(session.getInterviewType()).isEqualTo(InterviewType.ALL);
        assertThat(session.getCsSubjects()).hasSize(1);
        assertThat(session.getPortfolioLinks()).containsExactly("https://github.com/user/project");
    }

    @Test
    @DisplayName("질문 개수가 5 미만이면 예외가 발생한다")
    void createSessionWithQuestionCountTooLow() {
        // given
        Long userId = 1L;
        CreateInterviewSessionCommand command = new CreateInterviewSessionCommand(
                InterviewType.CS, Difficulty.JUNIOR, 4, InterviewerPersonality.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of());

        // when & then
        assertThatThrownBy(() -> interviewSessionService.create(userId, command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_QUESTION_COUNT.getMessage());
    }

    @Test
    @DisplayName("질문 개수가 10 초과이면 예외가 발생한다")
    void createSessionWithQuestionCountTooHigh() {
        // given
        Long userId = 1L;
        CreateInterviewSessionCommand command = new CreateInterviewSessionCommand(
                InterviewType.CS, Difficulty.JUNIOR, 11, InterviewerPersonality.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of());

        // when & then
        assertThatThrownBy(() -> interviewSessionService.create(userId, command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_QUESTION_COUNT.getMessage());
    }

    @Test
    @DisplayName("CS 면접에 상세 분야가 없으면 예외가 발생한다")
    void createCsSessionWithoutCsSubjects() {
        // given
        Long userId = 1L;
        CreateInterviewSessionCommand command = new CreateInterviewSessionCommand(
                InterviewType.CS, Difficulty.JUNIOR, 5, InterviewerPersonality.FRIENDLY,
                List.of(), List.of());

        // when & then
        assertThatThrownBy(() -> interviewSessionService.create(userId, command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CS_SUBJECT_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("포트폴리오 면접에 포트폴리오 링크가 없으면 예외가 발생한다")
    void createPortfolioSessionWithoutPortfolioLinks() {
        // given
        Long userId = 1L;
        CreateInterviewSessionCommand command = new CreateInterviewSessionCommand(
                InterviewType.PORTFOLIO, Difficulty.JUNIOR, 5, InterviewerPersonality.FRIENDLY,
                List.of(), List.of());

        // when & then
        assertThatThrownBy(() -> interviewSessionService.create(userId, command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PORTFOLIO_LINK_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("전체 면접에 상세 분야가 없으면 예외가 발생한다")
    void createAllSessionWithoutCsSubjects() {
        // given
        Long userId = 1L;
        CreateInterviewSessionCommand command = new CreateInterviewSessionCommand(
                InterviewType.ALL, Difficulty.JUNIOR, 5, InterviewerPersonality.FRIENDLY,
                List.of(), List.of("https://github.com/user/project"));

        // when & then
        assertThatThrownBy(() -> interviewSessionService.create(userId, command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CS_SUBJECT_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("전체 면접에 포트폴리오 링크가 없으면 예외가 발생한다")
    void createAllSessionWithoutPortfolioLinks() {
        // given
        Long userId = 1L;
        CreateInterviewSessionCommand command = new CreateInterviewSessionCommand(
                InterviewType.ALL, Difficulty.JUNIOR, 5, InterviewerPersonality.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of());

        // when & then
        assertThatThrownBy(() -> interviewSessionService.create(userId, command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PORTFOLIO_LINK_REQUIRED.getMessage());
    }
}