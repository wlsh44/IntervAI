package wlsh.project.intervai.report.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wlsh.project.intervai.common.auth.domain.UserInfo;
import wlsh.project.intervai.report.application.InterviewReportService;
import wlsh.project.intervai.report.domain.InterviewReport;
import wlsh.project.intervai.report.presentation.dto.InterviewReportResponse;

@RestController
@RequestMapping("/api/interviews/{interviewId}")
@RequiredArgsConstructor
public class InterviewReportController {

    private final InterviewReportService interviewReportService;

    @GetMapping("/report")
    public ResponseEntity<InterviewReportResponse> getReport(
            @AuthenticationPrincipal UserInfo userInfo,
            @PathVariable Long interviewId) {
        InterviewReport report = interviewReportService.getReport(userInfo.userId(), interviewId);
        return ResponseEntity.ok(InterviewReportResponse.from(report));
    }
}
