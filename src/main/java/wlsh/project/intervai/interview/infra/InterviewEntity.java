package wlsh.project.intervai.interview.infra;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.common.entity.BaseEntity;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;

@Getter
@Entity
@Table(name = "interviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobCategory jobCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewType interviewType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false)
    private int questionCount;

    @Column(nullable = false)
    private int maxFollowUpCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewerTone interviewerTone;

    private InterviewEntity(Long userId, JobCategory jobCategory, InterviewType interviewType,
                            Difficulty difficulty, int questionCount, int maxFollowUpCount,
                            InterviewerTone interviewerTone) {
        this.userId = userId;
        this.jobCategory = jobCategory;
        this.interviewType = interviewType;
        this.difficulty = difficulty;
        this.questionCount = questionCount;
        this.maxFollowUpCount = maxFollowUpCount;
        this.interviewerTone = interviewerTone;
    }

    public static InterviewEntity from(Interview interview) {
        return new InterviewEntity(
                interview.getUserId(), interview.getJobCategory(), interview.getInterviewType(),
                interview.getDifficulty(), interview.getQuestionCount(),
                interview.getMaxFollowUpCount(), interview.getInterviewerTone()
        );
    }

    public Interview toDomain(List<CsSubject> csSubjects, List<String> portfolioLinks, List<String> techStacks) {
        return Interview.of(id, userId, jobCategory, interviewType, difficulty, questionCount,
                maxFollowUpCount, interviewerTone, csSubjects, portfolioLinks, techStacks);
    }

    public Interview toDomain() {
        return Interview.of(id, userId, jobCategory, interviewType, difficulty, questionCount,
                maxFollowUpCount, interviewerTone, List.of(), List.of(), List.of());
    }

    public boolean isOwner(Long userId) {
        return this.userId.equals(userId);
    }
}
