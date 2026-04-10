package wlsh.project.intervai.profile.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.profile.infra.ProfileRepository;

@Component
@RequiredArgsConstructor
public class ProfileValidator {

    private final ProfileRepository profileRepository;

    public void validateProfileNotExists(Long userId) {
        profileRepository.findByUserIdAndStatus(userId, EntityStatus.ACTIVE)
                .ifPresent(profile -> {
                    throw new CustomException(ErrorCode.PROFILE_ALREADY_EXISTS);
                });
    }
}
