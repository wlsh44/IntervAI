package wlsh.project.intervai.interview.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Interview {

    private static final int DEFAULT_MAX_FOLLOW_UP_COUNT = 3;

    private final Long id;
    private final Long userId;
    private final InterviewType interviewType;
    private final Difficulty difficulty;
    private final int questionCount;
    private final int maxFollowUpCount;
    private final InterviewerTone interviewerTone;
    private final List<CsSubject> csSubjects;
    private final List<String> portfolioLinks;

    private Interview(Long id, Long userId, InterviewType interviewType, Difficulty difficulty,
                      int questionCount, int maxFollowUpCount, InterviewerTone interviewerTone,
                      List<CsSubject> csSubjects, List<String> portfolioLinks) {
        this.id = id;
        this.userId = userId;
        this.interviewType = interviewType;
        this.difficulty = difficulty;
        this.questionCount = questionCount;
        this.maxFollowUpCount = maxFollowUpCount;
        this.interviewerTone = interviewerTone;
        this.csSubjects = csSubjects;
        this.portfolioLinks = portfolioLinks;
    }

    public static Interview create(Long userId, CreateInterviewCommand command) {
        return new Interview(
                null, userId,
                command.interviewType(), command.difficulty(),
                command.questionCount(), DEFAULT_MAX_FOLLOW_UP_COUNT, command.interviewerTone(),
                command.csSubjects(), command.portfolioLinks()
        );
    }

    public static Interview of(Long id, Long userId, InterviewType interviewType, Difficulty difficulty,
                                int questionCount, int maxFollowUpCount, InterviewerTone interviewerTone,
                                List<CsSubject> csSubjects, List<String> portfolioLinks) {
        return new Interview(id, userId, interviewType, difficulty, questionCount, maxFollowUpCount,
                interviewerTone, csSubjects, portfolioLinks);
    }
}
