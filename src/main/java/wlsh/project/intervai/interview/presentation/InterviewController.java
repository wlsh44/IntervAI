package wlsh.project.intervai.interview.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wlsh.project.intervai.common.auth.domain.UserInfo;
import wlsh.project.intervai.interview.application.InterviewService;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewSummary;
import wlsh.project.intervai.interview.presentation.dto.CreateInterviewRequest;
import wlsh.project.intervai.interview.presentation.dto.CreateInterviewResponse;
import wlsh.project.intervai.interview.presentation.dto.InterviewListResponse;
import wlsh.project.intervai.session.application.InterviewSessionService;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;
    private final InterviewSessionService interviewSessionService;

    @GetMapping
    public ResponseEntity<InterviewListResponse> getList(
            @AuthenticationPrincipal UserInfo userInfo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<InterviewSummary> summaries = interviewService.getList(userInfo.userId(), PageRequest.of(page, size));
        return ResponseEntity.ok(InterviewListResponse.of(summaries));
    }

    @PostMapping
    public ResponseEntity<CreateInterviewResponse> create(
            @AuthenticationPrincipal UserInfo userInfo,
            @Valid @RequestBody CreateInterviewRequest request) {
        Interview interview = interviewService.create(userInfo.userId(), request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateInterviewResponse.of(interview));
    }

    @PostMapping("/{interviewId}/finish")
    public ResponseEntity<Void> finish(
            @AuthenticationPrincipal UserInfo userInfo,
            @PathVariable Long interviewId) {
        interviewSessionService.finish(userInfo.userId(), interviewId);
        return ResponseEntity.ok().build();
    }
}
