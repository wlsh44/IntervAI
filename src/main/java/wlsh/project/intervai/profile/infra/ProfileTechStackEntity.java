package wlsh.project.intervai.profile.infra;

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
@Table(name = "profile_tech_stacks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileTechStackEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long profileId;

    @Column(nullable = false)
    private Long techStackId;

    private ProfileTechStackEntity(Long profileId, Long techStackId) {
        this.profileId = profileId;
        this.techStackId = techStackId;
    }

    public static ProfileTechStackEntity of(Long profileId, Long techStackId) {
        return new ProfileTechStackEntity(profileId, techStackId);
    }
}
