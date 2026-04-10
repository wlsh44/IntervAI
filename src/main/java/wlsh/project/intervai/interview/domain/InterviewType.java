package wlsh.project.intervai.interview.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewType {
    CS("CS 기술 면접"),
    PORTFOLIO("포트폴리오 면접"),
    ALL("CS + 포트폴리오 통합 면접");

    private final String ko;
}
