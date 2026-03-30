package wlsh.project.intervai.profile.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Profile {

    private final Long id;
    private final Long userId;
    private final JobCategory jobCategory;
    private final CareerLevel careerLevel;
    private final List<String> techStacks;
    private final List<String> portfolioLinks;

    private Profile(Long id, Long userId, JobCategory jobCategory, CareerLevel careerLevel,
                    List<String> techStacks, List<String> portfolioLinks) {
        this.id = id;
        this.userId = userId;
        this.jobCategory = jobCategory;
        this.careerLevel = careerLevel;
        this.techStacks = techStacks;
        this.portfolioLinks = portfolioLinks;
    }

    public static Profile create(Long userId, CreateProfileCommand command) {
        return new Profile(null, userId, command.jobCategory(), command.careerLevel(),
                command.techStacks(), command.portfolioLinks());
    }

    public static Profile of(Long id, Long userId, JobCategory jobCategory, CareerLevel careerLevel,
                              List<String> techStacks, List<String> portfolioLinks) {
        return new Profile(id, userId, jobCategory, careerLevel, techStacks, portfolioLinks);
    }

}
