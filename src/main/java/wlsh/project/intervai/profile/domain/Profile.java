package wlsh.project.intervai.profile.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import wlsh.project.intervai.common.domain.JobCategory;

@Getter
public class Profile {

    private final Long id;
    private final Long userId;
    private final JobCategory jobCategory;
    private final CareerLevel careerLevel;
    private final List<String> techStacks;
    private final List<String> portfolioLinks;
    private final LocalDateTime updatedAt;

    private Profile(Long id, Long userId, JobCategory jobCategory, CareerLevel careerLevel,
                    List<String> techStacks, List<String> portfolioLinks, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.jobCategory = jobCategory;
        this.careerLevel = careerLevel;
        this.techStacks = techStacks;
        this.portfolioLinks = portfolioLinks;
        this.updatedAt = updatedAt;
    }

    public static Profile create(Long userId, CreateProfileCommand command) {
        return new Profile(null, userId, command.jobCategory(), command.careerLevel(),
                command.techStacks(), command.portfolioLinks(), null);
    }

    public static Profile of(Long id, Long userId, JobCategory jobCategory, CareerLevel careerLevel,
                              List<String> techStacks, List<String> portfolioLinks, LocalDateTime updatedAt) {
        return new Profile(id, userId, jobCategory, careerLevel, techStacks, portfolioLinks, updatedAt);
    }

}
