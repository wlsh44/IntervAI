package wlsh.project.intervai.profile.domain;

import java.util.List;

public record UpdateProfileCommand(
        JobCategory jobCategory,
        CareerLevel careerLevel,
        List<String> techStacks,
        List<String> portfolioLinks
) {
}
