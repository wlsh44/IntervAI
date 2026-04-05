package wlsh.project.intervai.interview.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CsCategory {
    DATA_STRUCTURE("자료구조"),
    ALGORITHM("알고리즘"),
    NETWORK("네트워크"),
    LANGUAGE("언어"),
    DATABASE("데이터베이스");

    private final String ko;
}
