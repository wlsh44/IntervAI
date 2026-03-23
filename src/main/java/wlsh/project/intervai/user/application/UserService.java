package wlsh.project.intervai.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.auth.application.TokenPairGenerator;
import wlsh.project.intervai.auth.domain.TokenPair;
import wlsh.project.intervai.user.domain.CreateUserCommand;
import wlsh.project.intervai.user.domain.CreateUserResult;
import wlsh.project.intervai.user.domain.User;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserValidator userValidator;
    private final UserManager userManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenPairGenerator tokenPairGenerator;

    public CreateUserResult create(CreateUserCommand command) {
        userValidator.validateCreateUser(command.nickname(), command.password());

        String encodedPassword = passwordEncoder.encode(command.password());
        User user = userManager.create(command.nickname(), encodedPassword);

        TokenPair tokenPair = tokenPairGenerator.createTokenPair(user.getId());

        return new CreateUserResult(user, tokenPair.accessToken(), tokenPair.refreshToken());
    }
}
