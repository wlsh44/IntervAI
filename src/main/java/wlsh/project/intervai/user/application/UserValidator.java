package wlsh.project.intervai.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.user.infra.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private static final int NICKNAME_MIN_LENGTH = 4;
    private static final int NICKNAME_MAX_LENGTH = 8;
    private static final int PASSWORD_MIN_LENGTH = 4;
    private static final int PASSWORD_MAX_LENGTH = 12;

    private final UserRepository userRepository;

    public void validateCreateUser(String nickname, String password) {
        validateNicknameLength(nickname);
        validatePasswordLength(password);
        validateNicknameNotDuplicate(nickname);
    }

    private void validateNicknameLength(String nickname) {
        if (nickname.length() < NICKNAME_MIN_LENGTH || nickname.length() > NICKNAME_MAX_LENGTH) {
            throw new CustomException(ErrorCode.INVALID_NICKNAME_LENGTH);
        }
    }

    private void validatePasswordLength(String password) {
        if (password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_LENGTH);
        }
    }

    private void validateNicknameNotDuplicate(String nickname) {
        if (userRepository.existsByNicknameAndStatus(nickname, EntityStatus.ACTIVE)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
