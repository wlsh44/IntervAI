package wlsh.project.intervai.profile.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.profile.domain.Profile;
import wlsh.project.intervai.profile.infra.PortfolioLinkEntity;
import wlsh.project.intervai.profile.infra.PortfolioLinkRepository;
import wlsh.project.intervai.profile.infra.ProfileEntity;
import wlsh.project.intervai.profile.infra.ProfileRepository;
import wlsh.project.intervai.profile.infra.ProfileTechStackEntity;
import wlsh.project.intervai.profile.infra.ProfileTechStackRepository;
import wlsh.project.intervai.profile.infra.TechStackEntity;
import wlsh.project.intervai.profile.infra.TechStackRepository;

@Component
@RequiredArgsConstructor
public class ProfileFinder {

    private final ProfileRepository profileRepository;
    private final TechStackRepository techStackRepository;
    private final PortfolioLinkRepository portfolioLinkRepository;
    private final ProfileTechStackRepository profileTechStackRepository;

    public Profile findById(Long profileId) {
        ProfileEntity profileEntity = profileRepository.findByIdAndStatus(profileId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND));

        List<Long> techStackIds = profileTechStackRepository.findAllByProfileIdAndStatus(profileEntity.getId(), EntityStatus.ACTIVE)
                .stream()
                .map(ProfileTechStackEntity::getTechStackId)
                .toList();

        List<String> techStacks = techStackRepository.findAllById(techStackIds)
                .stream()
                .map(TechStackEntity::getName)
                .toList();

        List<String> portfolioLinks = portfolioLinkRepository.findAllByProfileIdAndStatus(profileEntity.getId(), EntityStatus.ACTIVE)
                .stream()
                .map(PortfolioLinkEntity::getUrl)
                .toList();

        return profileEntity.toDomain(techStacks, portfolioLinks);
    }

}
