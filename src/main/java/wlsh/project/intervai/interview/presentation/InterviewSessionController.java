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
import wlsh.project.intervai.interview.application.InterviewSessionService;
import wlsh.project.intervai.interview.domain.InterviewSession;
import wlsh.project.intervai.interview.presentation.dto.CreateInterviewSessionRequest;
import wlsh.project.intervai.interview.presentation.dto.CreateInterviewSessionResponse;

@RestController
@RequestMapping("/api/interview-sessions")
@RequiredArgsConstructor
public class InterviewSessionController {

    private final InterviewSessionService interviewSessionService;

    @PostMapping
    public ResponseEntity<CreateInterviewSessionResponse> create(
            @AuthenticationPrincipal UserInfo userInfo,
            @Valid @RequestBody CreateInterviewSessionRequest request) {
        InterviewSession session = interviewSessionService.create(userInfo.userId(), request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateInterviewSessionResponse.of(session));
    }
}
