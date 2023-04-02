package kr.jm.openai.dto;

public interface DefaultOpenAiCompletionsRequestInterface {
    DefaultOpenAiCompletionsRequestInterface setModel(String model);

    DefaultOpenAiCompletionsRequestInterface setMaxTokens(Integer maxTokens);

    DefaultOpenAiCompletionsRequestInterface setTemperature(Double temperature);

    DefaultOpenAiCompletionsRequestInterface setTopP(Double topP);

    DefaultOpenAiCompletionsRequestInterface setN(Integer n);

    DefaultOpenAiCompletionsRequestInterface setStream(Boolean stream);

    DefaultOpenAiCompletionsRequestInterface setStop(java.util.List<String> stop);

    DefaultOpenAiCompletionsRequestInterface setPresencePenalty(Double presencePenalty);

    DefaultOpenAiCompletionsRequestInterface setFrequencyPenalty(Double frequencyPenalty);
}
