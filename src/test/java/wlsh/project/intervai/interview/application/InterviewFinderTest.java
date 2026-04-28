package wlsh.project.intervai.interview.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;
import wlsh.project.intervai.question.infra.QuestionPromptBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewFinderTest extends IntegrationTest {

    @Autowired
    private InterviewManager interviewManager;

    @Autowired
    private InterviewFinder interviewFinder;

    @Autowired
    private QuestionPromptBuilder questionPromptBuilder;

    @Test
    @DisplayName("면접 조회 시 CS 분야와 포트폴리오 링크를 함께 복원한다")
    void findRestoresInterviewDetails() {
        Interview saved = interviewManager.create(1L, new CreateInterviewCommand(
                JobCategory.BACKEND,
                InterviewType.ALL,
                Difficulty.JUNIOR,
                5,
                InterviewerTone.NORMAL,
                List.of(
                        CsSubject.of(CsCategory.NETWORK, List.of("HTTP", "TCP")),
                        CsSubject.of(CsCategory.DATABASE, List.of("INDEX"))
                ),
                List.of("https://github.com/user/repo"),
                List.of()
        ));

        Interview found = interviewFinder.find(saved.getId());

        assertThat(found.getCsSubjects()).hasSize(2);
        assertThat(found.getCsSubjects().get(0).getCategory()).isEqualTo(CsCategory.NETWORK);
        assertThat(found.getCsSubjects().get(0).getTopics()).containsExactly("HTTP", "TCP");
        assertThat(found.getCsSubjects().get(1).getCategory()).isEqualTo(CsCategory.DATABASE);
        assertThat(found.getCsSubjects().get(1).getTopics()).containsExactly("INDEX");
        assertThat(found.getPortfolioLinks()).containsExactly("https://github.com/user/repo");

        String prompt = questionPromptBuilder.build(found);
        assertThat(prompt)
                .contains("NETWORK")
                .contains("HTTP")
                .contains("TCP")
                .contains("DATABASE")
                .contains("INDEX")
                .contains("https://github.com/user/repo");
    }
}
