package wlsh.project.intervai.interview.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class CsSubject {

    public static final String ALL_TOPICS = "ALL";

    private final CsCategory category;
    private final List<String> topics;

    private CsSubject(CsCategory category, List<String> topics) {
        this.category = category;
        this.topics = topics == null ? List.of() : List.copyOf(topics);
    }

    public static CsSubject of(CsCategory category, List<String> topics) {
        return new CsSubject(category, topics);
    }

    public boolean isAllTopics() {
        return topics.size() == 1 && ALL_TOPICS.equals(topics.getFirst());
    }
}
