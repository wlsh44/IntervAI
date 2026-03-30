package wlsh.project.intervai.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.common.auth.application.TokenPairGenerator;
import wlsh.project.intervai.common.auth.domain.TokenPair;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.user.domain.CreateUserCommand;
import wlsh.project.intervai.user.domain.CreateUserResult;
import wlsh.project.intervai.user.domain.LoginCommand;
import wlsh.project.intervai.user.domain.LoginResult;
import wlsh.project.intervai.user.domain.User;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserValidator userValidator;
    private final UserManager userManager;
    private final UserFinder userFinder;
    private final PasswordEncoder passwordEncoder;
    private final TokenPairGenerator tokenPairGenerator;

    public CreateUserResult create(CreateUserCommand command) {
        userValidator.validateCreateUser(command.nickname(), command.password());

        String encodedPassword = passwordEncoder.encode(command.password());
        User user = userManager.create(command.nickname(), encodedPassword);

        TokenPair tokenPair = tokenPairGenerator.createTokenPair(user.getId());

        return new CreateUserResult(user, tokenPair.accessToken(), tokenPair.refreshToken());
    }

    public LoginResult login(LoginCommand command) {
        User user = userFinder.findByNickname(command.nickname());

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        TokenPair tokenPair = tokenPairGenerator.createTokenPair(user.getId());

        return new LoginResult(user, tokenPair.accessToken(), tokenPair.refreshToken());
    }
}
