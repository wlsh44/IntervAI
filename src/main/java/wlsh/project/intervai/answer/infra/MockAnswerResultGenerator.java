package wlsh.project.intervai.answer.infra;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.answer.application.AnswerResultGenerator;
import wlsh.project.intervai.answer.application.dto.AnswerResultDto;
import wlsh.project.intervai.interview.domain.Interview;

@Component
@Profile("!prod")
public class MockAnswerResultGenerator implements AnswerResultGenerator {

    @Override
    public AnswerResultDto generate(String conversationId, Interview interview, String question, String answer) {
        return new AnswerResultDto(
                "[Mock] 답변에 대한 피드백입니다. 난이도: " + interview.getDifficulty().getKo(),
                "[Mock] 꼬리 질문입니다."
        );
    }
}
