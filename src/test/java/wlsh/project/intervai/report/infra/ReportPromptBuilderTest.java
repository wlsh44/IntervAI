package wlsh.project.intervai.report.infra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReportPromptBuilderTest {

    private ReportPromptBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new ReportPromptBuilder(new ClassPathResource("prompts/summary.st"));
    }

    @Test
    @DisplayName("면접 유형, 난이도, 직군이 프롬프트에 포함된다")
    void buildContainsInterviewMetadata() {
        Interview interview = Interview.create(1L, new CreateInterviewCommand(
                JobCategory.BACKEND,
                InterviewType.CS,
                Difficulty.JUNIOR,
                5,
                InterviewerTone.NORMAL,
                List.of(CsSubject.of(CsCategory.NETWORK, List.of("HTTP"))),
                null,
                null));

        String prompt = builder.build(interview, "백엔드");

        assertThat(prompt)
                .contains(InterviewType.CS.name())
                .contains(Difficulty.JUNIOR.name())
                .contains("백엔드");
    }
}
