package wlsh.project.intervai.interview.application;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterviewValidatorTest extends IntegrationTest {

    @Autowired
    private InterviewValidator interviewValidator;

    @ParameterizedTest
    @ValueSource(ints = {2, 5, 10})
    @DisplayName("질문 개수가 2~10이면 검증을 통과한다")
    void validQuestionCount(int questionCount) {
        CreateInterviewCommand command = new CreateInterviewCommand(
                JobCategory.BACKEND, InterviewType.CS, Difficulty.JUNIOR, questionCount, InterviewerTone.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of(), List.of());

        assertThatNoException()
                .isThrownBy(() -> interviewValidator.validate(command));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 11, 20})
    @DisplayName("질문 개수가 2~10 범위 밖이면 예외가 발생한다")
    void invalidQuestionCount(int questionCount) {
        CreateInterviewCommand command = new CreateInterviewCommand(
                JobCategory.BACKEND, InterviewType.CS, Difficulty.JUNIOR, questionCount, InterviewerTone.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of(), List.of());

        assertThatThrownBy(() -> interviewValidator.validate(command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_QUESTION_COUNT.getMessage());
    }

    @Test
    @DisplayName("CS 면접에 상세 분야가 있으면 검증을 통과한다")
    void csInterviewWithSubjects() {
        CreateInterviewCommand command = new CreateInterviewCommand(
                JobCategory.BACKEND, InterviewType.CS, Difficulty.JUNIOR, 5, InterviewerTone.FRIENDLY,
                List.of(CsSubject.of(CsCategory.ALGORITHM, List.of("정렬"))), List.of(), List.of());

        assertThatNoException()
                .isThrownBy(() -> interviewValidator.validate(command));
    }

    @Test
    @DisplayName("CS 면접에 상세 분야가 없으면 예외가 발생한다")
    void csInterviewWithoutSubjects() {
        CreateInterviewCommand command = new CreateInterviewCommand(
                JobCategory.BACKEND, InterviewType.CS, Difficulty.JUNIOR, 5, InterviewerTone.FRIENDLY,
                List.of(), List.of(), List.of());

        assertThatThrownBy(() -> interviewValidator.validate(command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CS_SUBJECT_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("포트폴리오 면접에 링크가 있으면 검증을 통과한다")
    void portfolioInterviewWithLinks() {
        CreateInterviewCommand command = new CreateInterviewCommand(
                JobCategory.BACKEND, InterviewType.PORTFOLIO, Difficulty.JUNIOR, 5, InterviewerTone.FRIENDLY,
                List.of(), List.of("https://github.com/user/project"), List.of());

        assertThatNoException()
                .isThrownBy(() -> interviewValidator.validate(command));
    }

    @Test
    @DisplayName("포트폴리오 면접에 링크가 없으면 예외가 발생한다")
    void portfolioInterviewWithoutLinks() {
        CreateInterviewCommand command = new CreateInterviewCommand(
                JobCategory.BACKEND, InterviewType.PORTFOLIO, Difficulty.JUNIOR, 5, InterviewerTone.FRIENDLY,
                List.of(), List.of(), List.of());

        assertThatThrownBy(() -> interviewValidator.validate(command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PORTFOLIO_LINK_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("전체 면접에 상세 분야와 링크가 모두 있으면 검증을 통과한다")
    void allInterviewWithBoth() {
        CreateInterviewCommand command = new CreateInterviewCommand(
                JobCategory.BACKEND, InterviewType.ALL, Difficulty.JUNIOR, 5, InterviewerTone.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATABASE, List.of("인덱스"))),
                List.of("https://github.com/user/project"),
                List.of());

        assertThatNoException()
                .isThrownBy(() -> interviewValidator.validate(command));
    }

    @Test
    @DisplayName("전체 면접에 상세 분야가 없으면 예외가 발생한다")
    void allInterviewWithoutSubjects() {
        CreateInterviewCommand command = new CreateInterviewCommand(
                JobCategory.BACKEND, InterviewType.ALL, Difficulty.JUNIOR, 5, InterviewerTone.FRIENDLY,
                List.of(), List.of("https://github.com/user/project"), List.of());

        assertThatThrownBy(() -> interviewValidator.validate(command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CS_SUBJECT_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("전체 면접에 포트폴리오 링크가 없으면 예외가 발생한다")
    void allInterviewWithoutLinks() {
        CreateInterviewCommand command = new CreateInterviewCommand(
                JobCategory.BACKEND, InterviewType.ALL, Difficulty.JUNIOR, 5, InterviewerTone.FRIENDLY,
                List.of(CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map"))), List.of(), List.of());

        assertThatThrownBy(() -> interviewValidator.validate(command))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PORTFOLIO_LINK_REQUIRED.getMessage());
    }
}
