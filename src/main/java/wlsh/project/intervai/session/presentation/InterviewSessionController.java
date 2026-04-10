package wlsh.project.intervai.session.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wlsh.project.intervai.common.auth.domain.UserInfo;
import wlsh.project.intervai.interview.presentation.dto.CreateSessionResponse;
import wlsh.project.intervai.session.application.InterviewSessionService;
import wlsh.project.intervai.session.domain.InterviewSession;

@RestController
@RequestMapping("/api/interviews/{interviewId}/sessions")
@RequiredArgsConstructor
public class InterviewSessionController {

    private final InterviewSessionService interviewSessionService;

    @PostMapping
    public ResponseEntity<CreateSessionResponse> createSession(
            @AuthenticationPrincipal UserInfo userInfo,
            @PathVariable Long interviewId) {
        InterviewSession session = interviewSessionService.create(userInfo.userId(), interviewId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateSessionResponse.of(session));
    }

    @PostMapping("/finish")
    public ResponseEntity<Void> finish(
            @AuthenticationPrincipal UserInfo userInfo,
            @PathVariable Long interviewId) {
        interviewSessionService.finish(userInfo.userId(), interviewId);
        return ResponseEntity.ok().build();
    }
}
