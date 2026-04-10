package wlsh.project.intervai.profile.application;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.common.entity.BaseEntity;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.profile.domain.CreateProfileCommand;
import wlsh.project.intervai.profile.domain.Profile;
import wlsh.project.intervai.profile.domain.UpdateProfileCommand;
import wlsh.project.intervai.profile.infra.PortfolioLinkEntity;
import wlsh.project.intervai.profile.infra.PortfolioLinkRepository;
import wlsh.project.intervai.profile.infra.ProfileEntity;
import wlsh.project.intervai.profile.infra.ProfileRepository;
import wlsh.project.intervai.profile.infra.ProfileTechStackEntity;
import wlsh.project.intervai.profile.infra.ProfileTechStackRepository;
import wlsh.project.intervai.profile.infra.TechStackEntity;

@Component
@RequiredArgsConstructor
public class ProfileManager {

    private final ProfileRepository profileRepository;
    private final PortfolioLinkRepository portfolioLinkRepository;
    private final ProfileTechStackRepository profileTechStackRepository;
    private final TechStackManager techStackManager;

    @Transactional
    public Profile create(Long userId, CreateProfileCommand command) {
        Profile profile = Profile.create(userId, command);
        ProfileEntity profileEntity = profileRepository.save(ProfileEntity.from(profile));

        saveTechStacks(profileEntity.getId(), command.techStacks());
        savePortfolioLinks(profileEntity.getId(), command.portfolioLinks());

        return profileEntity.toDomain(command.techStacks(), command.portfolioLinks());
    }

    @Transactional
    public Profile update(Long profileId, UpdateProfileCommand command) {
        ProfileEntity profileEntity = profileRepository.findByIdAndStatus(profileId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND));

        return updateProfileEntity(profileEntity, command);
    }

    @Transactional
    public Profile updateByUserId(Long userId, UpdateProfileCommand command) {
        ProfileEntity profileEntity = profileRepository.findByUserIdAndStatus(userId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND));

        return updateProfileEntity(profileEntity, command);
    }

    private Profile updateProfileEntity(ProfileEntity profileEntity, UpdateProfileCommand command) {
        profileEntity.update(command.jobCategory(), command.careerLevel());

        profileTechStackRepository.findAllByProfileIdAndStatus(profileEntity.getId(), EntityStatus.ACTIVE)
                .forEach(BaseEntity::delete);
        portfolioLinkRepository.findAllByProfileIdAndStatus(profileEntity.getId(), EntityStatus.ACTIVE)
                .forEach(BaseEntity::delete);

        saveTechStacks(profileEntity.getId(), command.techStacks());
        savePortfolioLinks(profileEntity.getId(), command.portfolioLinks());

        return profileEntity.toDomain(command.techStacks(), command.portfolioLinks());
    }

    private void saveTechStacks(Long profileId, List<String> techStacks) {
        List<TechStackEntity> techStackEntities = techStackManager.findOrCreate(techStacks);
        List<ProfileTechStackEntity> entities = techStackEntities.stream()
                .map(techStack -> ProfileTechStackEntity.of(profileId, techStack.getId()))
                .toList();
        profileTechStackRepository.saveAll(entities);
    }

    private void savePortfolioLinks(Long profileId, List<String> portfolioLinks) {
        List<PortfolioLinkEntity> entities = portfolioLinks.stream()
                .map(url -> PortfolioLinkEntity.of(profileId, url))
                .toList();
        portfolioLinkRepository.saveAll(entities);
    }
}
