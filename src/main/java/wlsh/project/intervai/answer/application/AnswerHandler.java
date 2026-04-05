package wlsh.project.intervai.answer.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.answer.application.dto.AnswerResultDto;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.feedback.infra.FeedbackEntity;
import wlsh.project.intervai.feedback.infra.FeedbackRepository;
import wlsh.project.intervai.interview.infra.InterviewEntity;
import wlsh.project.intervai.interview.infra.InterviewRepository;
import wlsh.project.intervai.question.infra.QuestionEntity;
import wlsh.project.intervai.question.infra.QuestionRepository;

@Component
@RequiredArgsConstructor
public class AnswerHandler {

    private final AnswerResultGenerator answerResultGenerator;
    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final FeedbackRepository feedbackRepository;

    public AnswerResultDto submit(Long questionId, Answer answer) {
        QuestionEntity question = questionRepository.findByIdAndStatus(questionId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
        InterviewEntity interview = interviewRepository.findByIdAndStatus(answer.getInterviewId(), EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_FOUND));

        AnswerResultDto answerResult = answerResultGenerator.generate(String.valueOf(answer.getSessionId()), interview.toDomain(), question.getContent(), answer.getContent());
        questionRepository.save(QuestionEntity.createFollowUp(interview.getId(), question.getSessionId(), answerResult.followUpQuestion()));
        feedbackRepository.save(FeedbackEntity.create(answer.getId(), answerResult.feedback()));
        return answerResult;
    }
}
