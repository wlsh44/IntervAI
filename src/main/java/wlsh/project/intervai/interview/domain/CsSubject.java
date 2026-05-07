package wlsh.project.intervai.interview.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class CsSubject {

    private final CsCategory category;
    private final List<String> topics;

    private CsSubject(CsCategory category, List<String> topics) {
        this.category = category;
        this.topics = normalizeTopics(category, topics);
    }

    public static CsSubject of(CsCategory category, List<String> topics) {
        return new CsSubject(category, topics);
    }

    private static List<String> normalizeTopics(CsCategory category, List<String> topics) {
        if (topics == null) {
            return List.of();
        }
        if (topics.isEmpty() && category != null) {
            return category.getDefaultTopics();
        }
        return List.copyOf(topics);
    }
}
