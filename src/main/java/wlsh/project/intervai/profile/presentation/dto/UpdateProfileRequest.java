package wlsh.project.intervai.profile.presentation.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.profile.domain.CareerLevel;
import wlsh.project.intervai.profile.domain.UpdateProfileCommand;

public record UpdateProfileRequest(
        @NotNull(message = "희망 직무는 필수입니다.")
        JobCategory jobCategory,

        @NotNull(message = "경력 단계는 필수입니다.")
        CareerLevel careerLevel,

        @NotNull(message = "기술 스택은 필수입니다.")
        @Size(min = 1, max = 20, message = "기술 스택은 1개 이상 20개 이하여야 합니다.")
        List<@NotBlank(message = "기술 스택 이름은 비어있을 수 없습니다.") String> techStacks,

        @Size(max = 5, message = "포트폴리오 링크는 최대 5개까지 등록할 수 있습니다.")
        List<@NotBlank(message = "포트폴리오 링크는 비어있을 수 없습니다.") String> portfolioLinks
) {

    public UpdateProfileCommand toCommand() {
        return new UpdateProfileCommand(
                jobCategory,
                careerLevel,
                techStacks,
                portfolioLinks == null ? List.of() : portfolioLinks
        );
    }
}
