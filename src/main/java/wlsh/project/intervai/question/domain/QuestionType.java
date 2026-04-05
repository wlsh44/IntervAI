package wlsh.project.intervai.question.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionType {
    QUESTION("새로운 질문"),
    FOLLOW_UP("꼬리 질문");

    private final String ko;
}
