package wlsh.project.intervai.interview.domain;

import java.util.List;

import lombok.Getter;
import wlsh.project.intervai.common.domain.JobCategory;

@Getter
public class Interview {

    private static final int DEFAULT_MAX_FOLLOW_UP_COUNT = 3;

    private final Long id;
    private final Long userId;
    private final JobCategory jobCategory;
    private final InterviewType interviewType;
    private final Difficulty difficulty;
    private final int questionCount;
    private final int maxFollowUpCount;
    private final InterviewerTone interviewerTone;
    private final List<CsSubject> csSubjects;
    private final List<String> portfolioLinks;
    private final List<String> techStacks;

    private Interview(Long id, Long userId, JobCategory jobCategory, InterviewType interviewType,
                      Difficulty difficulty, int questionCount, int maxFollowUpCount,
                      InterviewerTone interviewerTone, List<CsSubject> csSubjects,
                      List<String> portfolioLinks, List<String> techStacks) {
        this.id = id;
        this.userId = userId;
        this.jobCategory = jobCategory;
        this.interviewType = interviewType;
        this.difficulty = difficulty;
        this.questionCount = questionCount;
        this.maxFollowUpCount = maxFollowUpCount;
        this.interviewerTone = interviewerTone;
        this.csSubjects = csSubjects;
        this.portfolioLinks = portfolioLinks;
        this.techStacks = techStacks;
    }

    public static Interview create(Long userId, CreateInterviewCommand command) {
        return new Interview(
                null, userId,
                command.jobCategory(), command.interviewType(), command.difficulty(),
                command.questionCount(), DEFAULT_MAX_FOLLOW_UP_COUNT, command.interviewerTone(),
                command.csSubjects(), command.portfolioLinks(), command.techStacks()
        );
    }

    public static Interview of(Long id, Long userId, JobCategory jobCategory,
                               InterviewType interviewType, Difficulty difficulty,
                               int questionCount, int maxFollowUpCount, InterviewerTone interviewerTone,
                               List<CsSubject> csSubjects, List<String> portfolioLinks, List<String> techStacks) {
        return new Interview(id, userId, jobCategory, interviewType, difficulty, questionCount,
                maxFollowUpCount, interviewerTone, csSubjects, portfolioLinks, techStacks);
    }
}
