package wlsh.project.intervai.interview.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wlsh.project.intervai.common.auth.domain.UserInfo;
import wlsh.project.intervai.interview.application.InterviewService;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.presentation.dto.CreateInterviewRequest;
import wlsh.project.intervai.interview.presentation.dto.CreateInterviewResponse;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<CreateInterviewResponse> create(
            @AuthenticationPrincipal UserInfo userInfo,
            @Valid @RequestBody CreateInterviewRequest request) {
        Interview interview = interviewService.create(userInfo.userId(), request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateInterviewResponse.of(interview));
    }
}
