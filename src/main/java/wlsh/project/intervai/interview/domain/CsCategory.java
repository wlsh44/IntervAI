package wlsh.project.intervai.interview.domain;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CsCategory {
    DATA_STRUCTURE("자료구조", List.of("Map", "List", "Set", "Stack", "Queue", "Tree", "Graph")),
    ALGORITHM("알고리즘", List.of("Sorting", "Dijkstra", "DFS/BFS", "Dynamic Programming")),
    NETWORK("네트워크", List.of("HTTP/HTTPS", "TCP/UDP", "DNS", "OSI 7 Layer")),
    LANGUAGE("언어", List.of("Java", "Python", "JavaScript", "TypeScript", "Go")),
    DATABASE("데이터베이스", List.of("Index", "Transaction", "Join", "Normalization"));

    private final String ko;
    private final List<String> defaultTopics;
}
