package wlsh.project.intervai.interview.presentation.dto;

import java.util.List;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;

public record CsSubjectResponse(
        CsCategory category,
        List<String> topics
) {
    public static CsSubjectResponse of(CsSubject csSubject) {
        return new CsSubjectResponse(csSubject.getCategory(), csSubject.getTopics());
    }
}
