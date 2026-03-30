package wlsh.project.intervai.profile.infra;

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
import wlsh.project.intervai.profile.domain.CareerLevel;
import wlsh.project.intervai.profile.domain.JobCategory;
import wlsh.project.intervai.profile.domain.Profile;

@Getter
@Entity
@Table(name = "profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private JobCategory jobCategory;

    @Enumerated(EnumType.STRING)
    private CareerLevel careerLevel;

    private ProfileEntity(Long userId, JobCategory jobCategory, CareerLevel careerLevel) {
        this.userId = userId;
        this.jobCategory = jobCategory;
        this.careerLevel = careerLevel;
    }

    public static ProfileEntity from(Profile profile) {
        return new ProfileEntity(profile.getUserId(), profile.getJobCategory(), profile.getCareerLevel());
    }

    public Profile toDomain(List<String> techStacks, List<String> portfolioLinks) {
        return Profile.of(id, userId, jobCategory, careerLevel, techStacks, portfolioLinks);
    }

    public void update(JobCategory jobCategory, CareerLevel careerLevel) {
        this.jobCategory = jobCategory;
        this.careerLevel = careerLevel;
    }

    public boolean isOwner(Long userId) {
        return this.userId.equals(userId);
    }
}
