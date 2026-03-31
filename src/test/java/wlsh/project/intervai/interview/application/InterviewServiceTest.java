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
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerPersonality;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterviewServiceTest extends IntegrationTest {

    @Autowired
    private InterviewService interviewService;

    @Test
    @DisplayName("CS 면접을 생성하면 면접 정보가 반환된다")
    void createCsInterview() {
        // given
        Long userId = 1L;
        List<CsSubject> csSubjects = List.of(
                CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map", "List")),
                CsSubject.of(CsCategory.ALGORITHM, List.of("정렬")));
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.CS, Difficulty.JUNIOR, 7, InterviewerPersonality.FRIENDLY,
                csSubjects, List.of());

        // when
        Interview interview = interviewService.create(userId, command);

        // then
        assertThat(interview.getId()).isNotNull();
        assertThat(interview.getUserId()).isEqualTo(userId);
        assertThat(interview.getInterviewType()).isEqualTo(InterviewType.CS);
        assertThat(interview.getDifficulty()).isEqualTo(Difficulty.JUNIOR);
        assertThat(interview.getQuestionCount()).isEqualTo(7);
        assertThat(interview.getInterviewerPersonality()).isEqualTo(InterviewerPersonality.FRIENDLY);
        assertThat(interview.getCsSubjects()).hasSize(2);
        assertThat(interview.getPortfolioLinks()).isEmpty();
    }

    @Test
    @DisplayName("포트폴리오 면접을 생성하면 면접 정보가 반환된다")
    void createPortfolioInterview() {
        // given
        Long userId = 1L;
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.PORTFOLIO, Difficulty.SENIOR, 5, InterviewerPersonality.AGGRESSIVE,
                List.of(), List.of("https://github.com/user/project"));

        // when
        Interview interview = interviewService.create(userId, command);

        // then
        assertThat(interview.getId()).isNotNull();
        assertThat(interview.getInterviewType()).isEqualTo(InterviewType.PORTFOLIO);
        assertThat(interview.getDifficulty()).isEqualTo(Difficulty.SENIOR);
        assertThat(interview.getQuestionCount()).isEqualTo(5);
        assertThat(interview.getInterviewerPersonality()).isEqualTo(InterviewerPersonality.AGGRESSIVE);
        assertThat(interview.getCsSubjects()).isEmpty();
        assertThat(interview.getPortfolioLinks()).containsExactly("https://github.com/user/project");
    }

    @Test
    @DisplayName("전체 면접을 생성하면 면접 정보가 반환된다")
    void createAllInterview() {
        // given
        Long userId = 1L;
        List<CsSubject> csSubjects = List.of(
                CsSubject.of(CsCategory.NETWORK, List.of("http/https", "tcp/udp")));
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.ALL, Difficulty.ENTRY, 10, InterviewerPersonality.NORMAL,
                csSubjects, List.of("https://github.com/user/project"));

        // when
        Interview interview = interviewService.create(userId, command);

        // then
        assertThat(interview.getId()).isNotNull();
        assertThat(interview.getInterviewType()).isEqualTo(InterviewType.ALL);
        assertThat(interview.getCsSubjects()).hasSize(1);
        assertThat(interview.getPortfolioLinks()).containsExactly("https://github.com/user/project");
    }

    @Test
    @DisplayName("질문 개수가 5 미만이면 예외가 발생한다")
    void createInterviewWithQuestionCountTooLow() {
        // given
        Long userId = 1L;
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.CS, Difficulty.JUNIOR, 4, InterviewerPersonality.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of());

        // when & then
        assertThatThrownBy(() -> interviewService.create(userId, command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_QUESTION_COUNT.getMessage());
    }

    @Test
    @DisplayName("질문 개수가 10 초과이면 예외가 발생한다")
    void createInterviewWithQuestionCountTooHigh() {
        // given
        Long userId = 1L;
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.CS, Difficulty.JUNIOR, 11, InterviewerPersonality.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of());

        // when & then
        assertThatThrownBy(() -> interviewService.create(userId, command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_QUESTION_COUNT.getMessage());
    }

    @Test
    @DisplayName("CS 면접에 상세 분야가 없으면 예외가 발생한다")
    void createCsInterviewWithoutCsSubjects() {
        // given
        Long userId = 1L;
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.CS, Difficulty.JUNIOR, 5, InterviewerPersonality.FRIENDLY,
                List.of(), List.of());

        // when & then
        assertThatThrownBy(() -> interviewService.create(userId, command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CS_SUBJECT_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("포트폴리오 면접에 포트폴리오 링크가 없으면 예외가 발생한다")
    void createPortfolioInterviewWithoutPortfolioLinks() {
        // given
        Long userId = 1L;
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.PORTFOLIO, Difficulty.JUNIOR, 5, InterviewerPersonality.FRIENDLY,
                List.of(), List.of());

        // when & then
        assertThatThrownBy(() -> interviewService.create(userId, command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PORTFOLIO_LINK_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("전체 면접에 상세 분야가 없으면 예외가 발생한다")
    void createAllInterviewWithoutCsSubjects() {
        // given
        Long userId = 1L;
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.ALL, Difficulty.JUNIOR, 5, InterviewerPersonality.FRIENDLY,
                List.of(), List.of("https://github.com/user/project"));

        // when & then
        assertThatThrownBy(() -> interviewService.create(userId, command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CS_SUBJECT_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("전체 면접에 포트폴리오 링크가 없으면 예외가 발생한다")
    void createAllInterviewWithoutPortfolioLinks() {
        // given
        Long userId = 1L;
        CreateInterviewCommand command = new CreateInterviewCommand(
                InterviewType.ALL, Difficulty.JUNIOR, 5, InterviewerPersonality.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of());

        // when & then
        assertThatThrownBy(() -> interviewService.create(userId, command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PORTFOLIO_LINK_REQUIRED.getMessage());
    }
}
