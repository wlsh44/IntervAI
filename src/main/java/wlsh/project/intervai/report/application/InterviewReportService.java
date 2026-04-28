package wlsh.project.intervai.report.application;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.report.application.InterviewReportContextFinder.InterviewReportContext;
import wlsh.project.intervai.report.application.dto.ReportGenerationResultDto;
import wlsh.project.intervai.report.domain.InterviewReport;
import wlsh.project.intervai.report.domain.ReportQuestion;
import wlsh.project.intervai.session.application.InterviewSessionValidator;
import wlsh.project.intervai.session.domain.InterviewSession;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewReportService {

    private final InterviewSessionValidator interviewSessionValidator;
    private final InterviewReportContextFinder interviewReportContextFinder;
    private final InterviewReportSessionValidator interviewReportSessionValidator;
    private final ReportQuestionAssembler reportQuestionAssembler;
    private final ReportGenerator reportGenerator;
    private final InterviewReportFinder interviewReportFinder;
    private final InterviewReportManager interviewReportManager;

    @Async
    public void requestGeneration(Long interviewId) {
        try {
            generateReport(interviewId);
        } catch (Exception e) {
            log.error("[InterviewReportService.requestGeneration] 리포트 생성 실패 - interviewId={}", interviewId, e);
        }
    }

    public void generateReport(Long interviewId) {
        InterviewReportContext context = interviewReportContextFinder.find(interviewId);
        Interview interview = context.interview();
        InterviewSession session = context.session();
        interviewReportSessionValidator.validateCompleted(session);

        ReportGenerationResultDto result = reportGenerator.generate(
                String.valueOf(session.getId()),
                interview,
                context.jobCategory()
        );
        List<ReportQuestion> questions = reportQuestionAssembler.assemble(interviewId, result);

        InterviewReport report = InterviewReport.create(
                interviewId,
                interview.getInterviewType(),
                context.jobCategory(),
                interview.getDifficulty(),
                interview.getQuestionCount(),
                session.getCompletedAt(),
                result.totalScore(),
                result.overallComment(),
                questions
        );
        interviewReportManager.create(report);
    }

    public InterviewReport getReport(Long userId, Long interviewId) {
        interviewSessionValidator.validateInterviewOwner(interviewId, userId);
        InterviewSession session = interviewReportContextFinder.find(interviewId).session();
        interviewReportSessionValidator.validateCompleted(session);
        StoredInterviewReport stored = interviewReportFinder.find(interviewId);
        ReportGenerationResultDto dto = new ReportGenerationResultDto(
                stored.totalScore(), stored.overallComment(), stored.keywords()
        );
        List<ReportQuestion> questions = reportQuestionAssembler.assemble(interviewId, dto);
        return new InterviewReport(
                stored.id(), stored.interviewId(), stored.interviewType(), stored.jobCategory(),
                stored.difficulty(), stored.questionCount(), stored.completedAt(),
                stored.totalScore(), stored.overallComment(), questions
        );
    }
}
