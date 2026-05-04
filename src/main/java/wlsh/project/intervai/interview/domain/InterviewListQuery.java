package wlsh.project.intervai.interview.domain;

import java.time.LocalDate;
import wlsh.project.intervai.session.domain.SessionStatus;

public record InterviewListQuery(
        String keyword,
        LocalDate startDate,
        LocalDate endDate,
        InterviewType interviewType,
        SessionStatus status
) {}
