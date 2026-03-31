package wlsh.project.intervai.interview.infra;

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
import wlsh.project.intervai.interview.domain.CsCategory;

@Getter
@Entity
@Table(name = "interview_cs_subjects")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewCsSubjectEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long interviewId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CsCategory category;

    @Column(nullable = false)
    private String topic;

    private InterviewCsSubjectEntity(Long interviewId, CsCategory category, String topic) {
        this.interviewId = interviewId;
        this.category = category;
        this.topic = topic;
    }

    public static InterviewCsSubjectEntity of(Long interviewId, CsCategory category, String topic) {
        return new InterviewCsSubjectEntity(interviewId, category, topic);
    }
}
