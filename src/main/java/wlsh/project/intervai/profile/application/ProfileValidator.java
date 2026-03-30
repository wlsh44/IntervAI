package wlsh.project.intervai.profile.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.profile.infra.ProfileEntity;
import wlsh.project.intervai.profile.infra.ProfileRepository;

@Component
@RequiredArgsConstructor
public class ProfileValidator {

    private final ProfileRepository profileRepository;

    public void validateProfileOwner(Long profileId, Long userId) {
        ProfileEntity profileEntity = profileRepository.findByIdAndStatus(profileId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND));
        if (!profileEntity.isOwner(userId)) {
            throw new CustomException(ErrorCode.PROFILE_ACCESS_DENIED);
        }
    }
}
