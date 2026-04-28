package wlsh.project.intervai.profile.domain;

import java.util.List;
import wlsh.project.intervai.common.domain.JobCategory;

public record UpdateProfileCommand(
        JobCategory jobCategory,
        CareerLevel careerLevel,
        List<String> techStacks,
        List<String> portfolioLinks
) {
}
