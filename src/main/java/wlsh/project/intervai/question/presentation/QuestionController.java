package wlsh.project.intervai.question.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wlsh.project.intervai.common.auth.domain.UserInfo;
import wlsh.project.intervai.interview.domain.NextQuestionResult;
import wlsh.project.intervai.interview.presentation.dto.CreateQuestionsResponse;
import wlsh.project.intervai.interview.presentation.dto.NextQuestionResponse;
import wlsh.project.intervai.question.application.QuestionService;
import wlsh.project.intervai.question.domain.Question;

import java.util.List;

@RestController
@RequestMapping("/api/interviews/{interviewId}/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("")
    public ResponseEntity<CreateQuestionsResponse> createQuestions(
            @AuthenticationPrincipal UserInfo userInfo,
            @PathVariable Long interviewId) {
        List<Question> questions = questionService.createAll(userInfo.userId(), interviewId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateQuestionsResponse.of(questions));
    }

    @GetMapping("/current")
    public ResponseEntity<NextQuestionResponse> currentQuestion(
            @AuthenticationPrincipal UserInfo userInfo,
            @PathVariable Long interviewId) {
        NextQuestionResult result = questionService.currentQuestion(userInfo.userId(), interviewId);
        return ResponseEntity.ok(NextQuestionResponse.of(result));
    }
}
