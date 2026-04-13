package wlsh.project.intervai.interview.presentation.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;

public record CreateInterviewRequest(
        @NotNull(message = "면접 유형은 필수입니다.")
        InterviewType interviewType,

        @NotNull(message = "숙련도는 필수입니다.")
        Difficulty difficulty,

        @NotNull(message = "질문 개수는 필수입니다.")
        Integer questionCount,

        @NotNull(message = "면접관 성격은 필수입니다.")
        InterviewerTone interviewerTone,

        List<@Valid CsSubjectRequest> csSubjects,

        List<@NotBlank(message = "포트폴리오 링크는 비어있을 수 없습니다.") String> portfolioLinks,

        List<String> techStacks
) {
    public CreateInterviewCommand toCommand() {
        return new CreateInterviewCommand(
                interviewType,
                difficulty,
                questionCount,
                interviewerTone,
                csSubjects == null ? List.of() : csSubjects.stream()
                        .map(CsSubjectRequest::toCsSubject)
                        .toList(),
                portfolioLinks == null ? List.of() : portfolioLinks,
                techStacks
        );
    }
}
