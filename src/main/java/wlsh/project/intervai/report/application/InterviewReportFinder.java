package wlsh.project.intervai.report.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.report.application.dto.ReportGenerationResultDto.QuestionKeywords;
import wlsh.project.intervai.report.infra.InterviewReportEntity;
import wlsh.project.intervai.report.infra.InterviewReportRepository;

@Component
@RequiredArgsConstructor
public class InterviewReportFinder {

    private final InterviewReportRepository interviewReportRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public StoredInterviewReport find(Long interviewId) {
        InterviewReportEntity entity = interviewReportRepository
                .findByInterviewIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));
        List<QuestionKeywords> keywords = readKeywords(entity.getQuestionsJson());
        return entity.toStoredReport(keywords);
    }

    private List<QuestionKeywords> readKeywords(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
