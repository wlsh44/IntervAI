package wlsh.project.intervai.profile.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.profile.domain.CreateProfileCommand;
import wlsh.project.intervai.profile.domain.Profile;
import wlsh.project.intervai.profile.domain.UpdateProfileCommand;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileFinder profileFinder;
    private final ProfileManager profileManager;
    private final ProfileValidator profileValidator;

    public Profile create(Long userId) {
        profileValidator.validateProfileNotExists(userId);
        return profileManager.create(userId, CreateProfileCommand.EMPTY_PROFILE);
    }

    public Profile getProfile(Long userId) {
        return profileFinder.findByUserId(userId);
    }

    public Profile updateProfile(Long userId, UpdateProfileCommand command) {
        return profileManager.updateByUserId(userId, command);
    }
}
