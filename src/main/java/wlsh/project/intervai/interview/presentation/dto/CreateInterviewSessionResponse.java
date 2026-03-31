package wlsh.project.intervai.interview.presentation.dto;

import java.util.List;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewSession;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerPersonality;

public record CreateInterviewSessionResponse(
        Long id,
        InterviewType interviewType,
        Difficulty difficulty,
        int questionCount,
        InterviewerPersonality interviewerPersonality,
        List<CsSubjectResponse> csSubjects,
        List<String> portfolioLinks
) {
    public static CreateInterviewSessionResponse of(InterviewSession session) {
        List<CsSubjectResponse> csSubjectResponses = session.getCsSubjects().stream()
                .map(CsSubjectResponse::of)
                .toList();
        return new CreateInterviewSessionResponse(
                session.getId(),
                session.getInterviewType(),
                session.getDifficulty(),
                session.getQuestionCount(),
                session.getInterviewerPersonality(),
                csSubjectResponses,
                session.getPortfolioLinks()
        );
    }
}
