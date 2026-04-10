package wlsh.project.intervai.interview.presentation.dto;

import java.util.List;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;

public record CreateInterviewResponse(
        Long id,
        InterviewType interviewType,
        Difficulty difficulty,
        int questionCount,
        InterviewerTone interviewerTone,
        List<CsSubjectResponse> csSubjects,
        List<String> portfolioLinks
) {
    public static CreateInterviewResponse of(Interview interview) {
        List<CsSubjectResponse> csSubjectResponses = interview.getCsSubjects().stream()
                .map(CsSubjectResponse::of)
                .toList();
        return new CreateInterviewResponse(
                interview.getId(),
                interview.getInterviewType(),
                interview.getDifficulty(),
                interview.getQuestionCount(),
                interview.getInterviewerTone(),
                csSubjectResponses,
                interview.getPortfolioLinks()
        );
    }
}
