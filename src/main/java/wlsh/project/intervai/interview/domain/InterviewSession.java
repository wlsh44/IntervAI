package wlsh.project.intervai.interview.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class InterviewSession {

    private final Long id;
    private final Long userId;
    private final InterviewType interviewType;
    private final Difficulty difficulty;
    private final int questionCount;
    private final InterviewerPersonality interviewerPersonality;
    private final List<CsSubject> csSubjects;
    private final List<String> portfolioLinks;

    private InterviewSession(Long id, Long userId, InterviewType interviewType, Difficulty difficulty,
                             int questionCount, InterviewerPersonality interviewerPersonality,
                             List<CsSubject> csSubjects, List<String> portfolioLinks) {
        this.id = id;
        this.userId = userId;
        this.interviewType = interviewType;
        this.difficulty = difficulty;
        this.questionCount = questionCount;
        this.interviewerPersonality = interviewerPersonality;
        this.csSubjects = csSubjects;
        this.portfolioLinks = portfolioLinks;
    }

    public static InterviewSession create(Long userId, CreateInterviewSessionCommand command) {
        return new InterviewSession(
                null, userId,
                command.interviewType(), command.difficulty(),
                command.questionCount(), command.interviewerPersonality(),
                command.csSubjects(), command.portfolioLinks()
        );
    }

    public static InterviewSession of(Long id, Long userId, InterviewType interviewType, Difficulty difficulty,
                                       int questionCount, InterviewerPersonality interviewerPersonality,
                                       List<CsSubject> csSubjects, List<String> portfolioLinks) {
        return new InterviewSession(id, userId, interviewType, difficulty, questionCount,
                interviewerPersonality, csSubjects, portfolioLinks);
    }
}
