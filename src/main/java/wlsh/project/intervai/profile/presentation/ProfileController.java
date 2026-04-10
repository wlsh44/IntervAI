package wlsh.project.intervai.profile.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wlsh.project.intervai.common.auth.domain.UserInfo;
import wlsh.project.intervai.profile.application.ProfileService;
import wlsh.project.intervai.profile.domain.Profile;
import wlsh.project.intervai.profile.presentation.dto.ProfileResponse;
import wlsh.project.intervai.profile.presentation.dto.UpdateProfileRequest;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(
            @AuthenticationPrincipal UserInfo userInfo) {
        Profile profile = profileService.create(userInfo.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProfileResponse.of(profile));
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthenticationPrincipal UserInfo userInfo) {
        Profile profile = profileService.getProfile(userInfo.userId());
        return ResponseEntity.ok(ProfileResponse.of(profile));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @AuthenticationPrincipal UserInfo userInfo,
            @Valid @RequestBody UpdateProfileRequest request) {
        Profile profile = profileService.updateProfile(userInfo.userId(), request.toCommand());
        return ResponseEntity.ok(ProfileResponse.of(profile));
    }
}
