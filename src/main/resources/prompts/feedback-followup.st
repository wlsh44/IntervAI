지원자의 답변을 평가하고, 꼬리 질문을 생성하세요.

[현재 질문]
{question}

[지원자 답변]
{answerText}

규칙:
- feedback은 답변의 좋은 점과 부족한 점을 구체적으로 평가하세요.
- feedback은 이전 대화의 면접관 성향에 맞는 톤으로 작성하세요.
- followUpQuestion은 답변의 부족하거나 흥미로운 부분을 파고드는 심화 질문 1개를 생성하세요.
- followUpQuestion은 이전 대화 히스토리를 고려하여 중복되지 않게 하세요.
- 모든 답변은 한글로 하세요.

출력 형식:
{
  "feedback": "피드백 내용",
  "followUpQuestion": "꼬리 질문 내용"
}