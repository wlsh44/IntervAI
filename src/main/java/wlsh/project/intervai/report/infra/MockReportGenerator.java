package wlsh.project.intervai.report.infra;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.report.application.ReportGenerator;
import wlsh.project.intervai.report.application.dto.ReportGenerationResultDto;

import java.util.List;

@Component
@Profile("!prod")
public class MockReportGenerator implements ReportGenerator {

    @Override
    public ReportGenerationResultDto generate(String sessionId, Interview interview, String jobCategory) {
        return new ReportGenerationResultDto(
                82,
                "전반적으로 기본기가 탄탄하며 논리적인 답변을 잘 구성했습니다. 세부 항목에서 더 깊이 있는 이해를 보여준다면 더욱 우수한 결과를 기대할 수 있습니다.",
                List.of()
        );
    }
}
