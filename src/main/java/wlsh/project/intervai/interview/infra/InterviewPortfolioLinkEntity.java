package wlsh.project.intervai.interview.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wlsh.project.intervai.common.entity.BaseEntity;

@Getter
@Entity
@Table(name = "interview_portfolio_links")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewPortfolioLinkEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long interviewId;

    @Column(nullable = false)
    private String url;

    private InterviewPortfolioLinkEntity(Long interviewId, String url) {
        this.interviewId = interviewId;
        this.url = url;
    }

    public static InterviewPortfolioLinkEntity of(Long interviewId, String url) {
        return new InterviewPortfolioLinkEntity(interviewId, url);
    }
}
