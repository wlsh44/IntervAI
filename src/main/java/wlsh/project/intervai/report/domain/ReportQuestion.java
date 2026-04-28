package wlsh.project.intervai.report.domain;

import java.util.List;

public record ReportQuestion(
        Long questionId,
        int questionIndex,
        String questionContent,
        String answerContent,
        String feedbackContent,
        Integer score,
        List<String> keywords,
        List<FollowUpQuestion> followUps
) {
    public record FollowUpQuestion(
            Long questionId,
            String questionContent,
            String answerContent,
            String feedbackContent
    ) {}
}
