package wlsh.project.intervai.interview.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class CsSubject {

    private final CsCategory category;
    private final List<String> topics;

    private CsSubject(CsCategory category, List<String> topics) {
        this.category = category;
        this.topics = topics;
    }

    public static CsSubject of(CsCategory category, List<String> topics) {
        return new CsSubject(category, topics);
    }
}
