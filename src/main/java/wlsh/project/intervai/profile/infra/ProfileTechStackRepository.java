package wlsh.project.intervai.profile.infra;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;

public interface ProfileTechStackRepository extends JpaRepository<ProfileTechStackEntity, Long> {

    List<ProfileTechStackEntity> findAllByProfileIdAndStatus(Long profileId, EntityStatus status);

}
