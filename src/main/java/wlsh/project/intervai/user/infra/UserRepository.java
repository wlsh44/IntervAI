package wlsh.project.intervai.user.infra;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import wlsh.project.intervai.common.entity.EntityStatus;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByNicknameAndStatus(String nickname, EntityStatus status);

    Optional<UserEntity> findByNicknameAndStatus(String nickname, EntityStatus status);

    Optional<UserEntity> findByIdAndStatus(Long id, EntityStatus status);
}
