package wlsh.project.intervai.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.user.domain.User;
import wlsh.project.intervai.user.infra.UserEntity;
import wlsh.project.intervai.user.infra.UserRepository;

@Component
@RequiredArgsConstructor
public class UserManager {

    private final UserRepository userRepository;

    @Transactional
    public User create(String nickname, String encodedPassword) {
        User user = User.create(nickname, encodedPassword);
        UserEntity savedEntity = userRepository.save(UserEntity.from(user));
        return savedEntity.toDomain();
    }
}
