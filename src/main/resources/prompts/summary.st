지원자의 전체 면접 내용을 바탕으로 종합 평가 리포트를 작성하세요.

이번 세션의 본 질문·답변·피드백·꼬리 질문 전체 대화 기록은 대화 컨텍스트(ChatMemory, conversation_id = sessionId)에 이미 포함되어 전달됩니다. 별도의 면접 내용 텍스트가 프롬프트에 포함되지 않아도 대화 컨텍스트를 근거로 평가하세요.

면접 정보:
- 면접 유형: {interviewType}
- 난이도: {difficulty}
- 직군: {jobCategory}

규칙:
- totalScore는 100점 만점으로 채점하세요. 각 답변 시점에 이미 산출·저장된 질문별 점수(Feedback.score)의 분포와, 대화 흐름상 드러난 일관성/태도/논리성을 종합적으로 고려하여 판단합니다. 단순 산술 평균이 아닌 종합 판단값입니다.
- overallComment는 면접 전반의 총평을 2~3문장 분량으로 자연스럽게 서술하세요. 잘한 점과 보완이 필요한 점을 함께 언급하되, 리스트가 아닌 하나의 문단으로 작성합니다.
- 난이도와 직군 수준에 맞게 기대치를 조정하여 평가하세요.
- questions 배열에는 본 질문(QUESTION)만 포함하며, 입력된 본 질문 순서(questionIndex 오름차순)를 그대로 유지합니다. 꼬리 질문(FOLLOW_UP)은 본 질문 평가에 참고만 하고 배열에는 포함하지 않습니다.
- 각 질문 항목(questions[])에는 아래 필드만 작성합니다. (질문별 점수 `score`는 LLM이 생성하지 않습니다. 답변 제출 시점의 피드백 단계에서 이미 채점되어 저장되어 있으며, 백엔드가 응답에 합쳐 반환합니다.)
  - questionId: 대화 컨텍스트에 포함된 해당 본 질문의 ID를 그대로 사용합니다. (임의 생성 금지)
  - keywords: 질문이 다루는 핵심 주제를 요약한 3개의 짧은 키워드. 반드시 영문 대문자로만 구성하고 공백·하이픈·특수문자를 포함하지 않으며 각 키워드는 최대 12자 이내입니다. (예: "VIRTUALDOM", "DIFFING", "RECONCILIATION")
- 모든 서술(overallComment 등)은 한글로 작성합니다. keywords만 영문 대문자로 작성합니다.
- 출력은 아래 JSON 스키마를 엄격히 준수하고, 추가 필드나 설명 텍스트를 포함하지 마세요.

출력 형식:
\{
  "totalScore": 88,
  "overallComment": "전반적으로 기술적 기초가 탄탄하며, 특히 React 생태계에 대한 이해도가 높습니다. 다만 일부 CS 구조 개념에서는 핵심 키워드를 더 정확히 짚어 설명하면 좋겠습니다.",
  "questions": [
    \{
      "questionId": 1,
      "keywords": ["VIRTUALDOM", "DIFFING", "RECONCILIATION"]
    \},
    \{
      "questionId": 2,
      "keywords": ["SCOPE", "ENCAPSULATION", "LEXICALENV"]
    \}
  ]
\}
