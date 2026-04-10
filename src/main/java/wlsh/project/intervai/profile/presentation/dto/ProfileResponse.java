package wlsh.project.intervai.profile.presentation.dto;

import java.time.LocalDateTime;
import java.util.List;
import wlsh.project.intervai.profile.domain.CareerLevel;
import wlsh.project.intervai.profile.domain.JobCategory;
import wlsh.project.intervai.profile.domain.Profile;

public record ProfileResponse(
        Long id,
        JobCategory jobCategory,
        CareerLevel careerLevel,
        List<String> techStacks,
        List<String> portfolioLinks,
        LocalDateTime updatedAt
) {

    public static ProfileResponse of(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getJobCategory(),
                profile.getCareerLevel(),
                profile.getTechStacks(),
                profile.getPortfolioLinks(),
                profile.getUpdatedAt()
        );
    }
}
