package wlsh.project.intervai.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.user.domain.User;
import wlsh.project.intervai.user.infra.UserRepository;

@Component
@RequiredArgsConstructor
public class UserFinder {

    private final UserRepository userRepository;

    public User findByNickname(String nickname) {
        return userRepository.findByNicknameAndStatus(nickname, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED))
                .toDomain();
    }

    public User findById(Long userId) {
        return userRepository.findByIdAndStatus(userId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .toDomain();
    }
}
