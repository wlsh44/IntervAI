package wlsh.project.intervai.interview.presentation.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;

public record CsSubjectRequest(
        @NotNull(message = "CS 카테고리는 필수입니다.")
        CsCategory category,

        @NotNull(message = "토픽 목록은 필수입니다.")
        @Size(min = 1, message = "토픽은 1개 이상이어야 합니다.")
        List<@NotBlank(message = "토픽은 비어있을 수 없습니다.") String> topics
) {
    public CsSubject toCsSubject() {
        return CsSubject.of(category, topics);
    }
}
