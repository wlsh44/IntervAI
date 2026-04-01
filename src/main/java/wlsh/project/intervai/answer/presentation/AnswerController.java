package wlsh.project.intervai.answer.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wlsh.project.intervai.answer.application.AnswerService;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.answer.presentation.dto.CreateAnswerRequest;
import wlsh.project.intervai.answer.presentation.dto.CreateAnswerResponse;
import wlsh.project.intervai.common.auth.domain.UserInfo;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("")
    public ResponseEntity<CreateAnswerResponse> create(
            @AuthenticationPrincipal UserInfo userInfo,
            @Valid @RequestBody CreateAnswerRequest request
            ) {
        Answer answer = answerService.create(userInfo.userId(), request.questionId(), request.content());
        return ResponseEntity.ok(CreateAnswerResponse.create(answer));
    }
}
