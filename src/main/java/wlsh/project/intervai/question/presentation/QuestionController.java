package wlsh.project.intervai.question.presentation;

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
import wlsh.project.intervai.question.application.QuestionService;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.presentation.dto.CreateQuestionRequest;
import wlsh.project.intervai.question.presentation.dto.CreateQuestionResponse;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<CreateQuestionResponse> create(
            @AuthenticationPrincipal UserInfo userInfo,
            @Valid @RequestBody CreateQuestionRequest request) {
        Question question = questionService.create(request.sessionId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateQuestionResponse.of(question));
    }
}
