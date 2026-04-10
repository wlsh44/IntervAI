package wlsh.project.intervai.interview.presentation.dto;

import java.util.List;
import org.springframework.data.domain.Page;
import wlsh.project.intervai.interview.domain.InterviewSummary;

public record InterviewListResponse(
        List<InterviewSummaryResponse> content,
        long totalElements,
        int totalPages,
        boolean last
) {

    public static InterviewListResponse of(Page<InterviewSummary> page) {
        List<InterviewSummaryResponse> content = page.getContent().stream()
                .map(InterviewSummaryResponse::of)
                .toList();
        return new InterviewListResponse(
                content,
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
