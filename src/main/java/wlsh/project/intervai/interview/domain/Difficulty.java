package wlsh.project.intervai.interview.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Difficulty {
    ENTRY("신입"),
    JUNIOR("주니어"),
    SENIOR("시니어");

    private final String ko;
}
