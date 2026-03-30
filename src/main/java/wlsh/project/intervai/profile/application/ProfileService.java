package wlsh.project.intervai.profile.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.profile.domain.Profile;
import wlsh.project.intervai.profile.domain.UpdateProfileCommand;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileFinder profileFinder;
    private final ProfileManager profileManager;
    private final ProfileValidator profileValidator;

    public Profile updateProfile(Long userId, Long profileId, UpdateProfileCommand command) {
        profileValidator.validateProfileOwner(profileId, userId);
        return profileManager.update(profileId, command);
    }

    public Profile getProfile(Long userId, Long profileId) {
        profileValidator.validateProfileOwner(profileId, userId);
        return profileFinder.findById(profileId);
    }
}
