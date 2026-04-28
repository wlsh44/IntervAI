package wlsh.project.intervai.report.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.ai.AiChatCaller;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.report.application.ReportGenerator;
import wlsh.project.intervai.report.application.dto.ReportGenerationResultDto;

@Slf4j
@Component
@Profile("prod")
public class ApiReportGenerator implements ReportGenerator {

    private final AiChatCaller aiChatCaller;
    private final ReportPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public ApiReportGenerator(AiChatCaller aiChatCaller,
                                 ReportPromptBuilder promptBuilder,
                                 ObjectMapper objectMapper) {
        this.aiChatCaller = aiChatCaller;
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
    }

    @Override
    public ReportGenerationResultDto generate(String sessionId, Interview interview, String jobCategory) {
        log.info("[ApiReportGenerator.generate] 리포트 생성 시작 - sessionId={}, interviewType={}, jobCategory={}",
                sessionId, interview.getInterviewType(), jobCategory);
        String prompt = promptBuilder.build(interview, jobCategory);
        String response = aiChatCaller.callWithSession(sessionId, prompt);
        ReportGenerationResultDto result = parseResult(response);
        log.info("[ApiReportGenerator.generate] 리포트 생성 완료 - totalScore={}, questions={}",
                result.totalScore(), result.questions().size());
        return result;
    }

    private ReportGenerationResultDto parseResult(String response) {
        try {
            return objectMapper.readValue(response.trim(), ReportGenerationResultDto.class);
        } catch (Exception e) {
            log.warn("[ApiReportGenerator.parseResult] JSON 파싱 실패, 응답:\n{}", response);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
