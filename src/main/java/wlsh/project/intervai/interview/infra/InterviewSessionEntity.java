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
import wlsh.project.intervai.common.entity.BaseEntity;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewSession;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerPersonality;

@Getter
@Entity
@Table(name = "interview_sessions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewSessionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewType interviewType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false)
    private int questionCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewerPersonality interviewerPersonality;

    private InterviewSessionEntity(Long userId, InterviewType interviewType, Difficulty difficulty,
                                    int questionCount, InterviewerPersonality interviewerPersonality) {
        this.userId = userId;
        this.interviewType = interviewType;
        this.difficulty = difficulty;
        this.questionCount = questionCount;
        this.interviewerPersonality = interviewerPersonality;
    }

    public static InterviewSessionEntity from(InterviewSession session) {
        return new InterviewSessionEntity(
                session.getUserId(), session.getInterviewType(), session.getDifficulty(),
                session.getQuestionCount(), session.getInterviewerPersonality()
        );
    }

    public InterviewSession toDomain(List<CsSubject> csSubjects, List<String> portfolioLinks) {
        return InterviewSession.of(id, userId, interviewType, difficulty, questionCount,
                interviewerPersonality, csSubjects, portfolioLinks);
    }
}
