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
@Table(name = "portfolio_links")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioLinkEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long profileId;

    @Column(nullable = false, length = 500)
    private String url;

    private PortfolioLinkEntity(Long profileId, String url) {
        this.profileId = profileId;
        this.url = url;
    }

    public static PortfolioLinkEntity of(Long profileId, String url) {
        return new PortfolioLinkEntity(profileId, url);
    }
}
