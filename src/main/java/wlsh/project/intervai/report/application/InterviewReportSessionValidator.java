package wlsh.project.intervai.report.application;

import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.session.domain.InterviewSession;

@Component
public class InterviewReportSessionValidator {

    public void validateCompleted(InterviewSession session) {
        if (!session.isCompleted()) {
            throw new CustomException(ErrorCode.SESSION_NOT_COMPLETED);
        }
    }
}
