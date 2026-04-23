package wlsh.project.intervai.report.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wlsh.project.intervai.common.entity.BaseEntity;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.report.application.StoredInterviewReport;
import wlsh.project.intervai.report.application.dto.ReportGenerationResultDto.QuestionKeywords;
import wlsh.project.intervai.report.domain.InterviewReport;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "interview_reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewReportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long interviewId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewType interviewType;

    @Column(nullable = false)
    private String jobCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false)
    private int questionCount;

    @Column
    private LocalDateTime completedAt;

    @Column(nullable = false)
    private int totalScore;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String overallComment;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionsJson;

    private InterviewReportEntity(Long interviewId, InterviewType interviewType, String jobCategory,
                                  Difficulty difficulty, int questionCount, LocalDateTime completedAt,
                                  int totalScore, String overallComment, String questionsJson) {
        this.interviewId = interviewId;
        this.interviewType = interviewType;
        this.jobCategory = jobCategory;
        this.difficulty = difficulty;
        this.questionCount = questionCount;
        this.completedAt = completedAt;
        this.totalScore = totalScore;
        this.overallComment = overallComment;
        this.questionsJson = questionsJson;
    }

    public static InterviewReportEntity from(InterviewReport report, String questionsJson) {
        return new InterviewReportEntity(
                report.interviewId(),
                report.interviewType(),
                report.jobCategory(),
                report.difficulty(),
                report.questionCount(),
                report.completedAt(),
                report.totalScore(),
                report.overallComment(),
                questionsJson
        );
    }

    public StoredInterviewReport toStoredReport(List<QuestionKeywords> keywords) {
        return new StoredInterviewReport(
                id, interviewId, interviewType, jobCategory, difficulty,
                questionCount, completedAt, totalScore, overallComment, keywords
        );
    }
}
