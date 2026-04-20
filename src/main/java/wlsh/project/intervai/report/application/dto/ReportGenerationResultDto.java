package wlsh.project.intervai.report.application.dto;

import java.util.List;

public record ReportGenerationResultDto(
        int totalScore,
        String overallComment,
        List<QuestionKeywords> questions
) {

    public record QuestionKeywords(
            Long questionId,
            List<String> keywords
    ) {
    }
}
