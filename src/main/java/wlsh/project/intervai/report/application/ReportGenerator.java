package wlsh.project.intervai.report.application;

import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.report.application.dto.ReportGenerationResultDto;

public interface ReportGenerator {

    ReportGenerationResultDto generate(String sessionId, Interview interview, String jobCategory);
}
