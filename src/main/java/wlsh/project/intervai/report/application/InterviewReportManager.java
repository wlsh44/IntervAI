package wlsh.project.intervai.report.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.report.domain.InterviewReport;
import wlsh.project.intervai.report.domain.ReportQuestion;
import wlsh.project.intervai.report.infra.InterviewReportEntity;
import wlsh.project.intervai.report.infra.InterviewReportRepository;

@Component
@RequiredArgsConstructor
public class InterviewReportManager {

    private final InterviewReportRepository interviewReportRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void create(InterviewReport report) {
        if (interviewReportRepository.existsByInterviewIdAndStatus(report.interviewId(), EntityStatus.ACTIVE)) {
            throw new CustomException(ErrorCode.REPORT_ALREADY_EXISTS);
        }
        String questionsJson = writeQuestions(report.questions());
        interviewReportRepository.save(InterviewReportEntity.from(report, questionsJson));
    }

    private String writeQuestions(List<ReportQuestion> questions) {
        try {
            return objectMapper.writeValueAsString(questions);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
