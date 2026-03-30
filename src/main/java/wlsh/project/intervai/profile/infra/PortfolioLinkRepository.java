package wlsh.project.intervai.profile.infra;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;

public interface PortfolioLinkRepository extends JpaRepository<PortfolioLinkEntity, Long> {

    List<PortfolioLinkEntity> findAllByProfileIdAndStatus(Long profileId, EntityStatus status);

}
