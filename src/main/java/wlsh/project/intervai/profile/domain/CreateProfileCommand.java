package wlsh.project.intervai.profile.domain;

import java.util.List;
import wlsh.project.intervai.common.domain.JobCategory;

public record CreateProfileCommand(
        JobCategory jobCategory,
        CareerLevel careerLevel,
        List<String> techStacks,
        List<String> portfolioLinks
) {

    public static final CreateProfileCommand EMPTY_PROFILE = new CreateProfileCommand(null, null, List.of(), List.of());
}
