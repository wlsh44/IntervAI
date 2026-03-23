package wlsh.project.intervai.user.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByNicknameAndStatus(String nickname, EntityStatus status);
}
