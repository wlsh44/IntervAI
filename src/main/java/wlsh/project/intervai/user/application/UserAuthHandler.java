package wlsh.project.intervai.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.profile.infra.ProfileEntity;
import wlsh.project.intervai.profile.infra.ProfileRepository;
import wlsh.project.intervai.user.domain.User;
import wlsh.project.intervai.user.infra.UserEntity;
import wlsh.project.intervai.user.infra.UserRepository;

@Component
@RequiredArgsConstructor
public class UserAuthHandler {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public User signUp(String nickname, String encodedPassword) {
        User user = User.create(nickname, encodedPassword);
        UserEntity savedUser = userRepository.save(UserEntity.from(user));

        profileRepository.save(ProfileEntity.ofEmpty(savedUser.getId()));

        return savedUser.toDomain();
    }
}
