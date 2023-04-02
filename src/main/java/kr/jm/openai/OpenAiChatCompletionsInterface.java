package kr.jm.openai;

import kr.jm.openai.dto.OpenAiChatCompletionsRequest;
import kr.jm.openai.dto.OpenAiChatCompletionsResponse;

public interface OpenAiChatCompletionsInterface extends
        OpenAiCompletionsInterface<OpenAiChatCompletionsRequest, OpenAiChatCompletionsResponse> {
}
