package wlsh.project.intervai.answer.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wlsh.project.intervai.answer.application.AnswerService;
import wlsh.project.intervai.answer.domain.AnswerResult;
import wlsh.project.intervai.answer.presentation.dto.CreateAnswerRequest;
import wlsh.project.intervai.answer.presentation.dto.CreateAnswerResponse;
import wlsh.project.intervai.common.auth.domain.UserInfo;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interviews/{interviewId}/answers")
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    public ResponseEntity<CreateAnswerResponse> answer(
            @AuthenticationPrincipal UserInfo userInfo,
            @PathVariable Long interviewId,
            @Valid @RequestBody CreateAnswerRequest request) {
        AnswerResult result = answerService.answer(
                userInfo.userId(), interviewId, request.questionId(), request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateAnswerResponse.of(result));
    }
}
