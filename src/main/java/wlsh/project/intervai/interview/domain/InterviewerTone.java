package wlsh.project.intervai.interview.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewerTone {
    FRIENDLY("친절한 면접관"),
    NORMAL("일반적인 면접관"),
    AGGRESSIVE("압박적인 면접관");

    private final String ko;
}
