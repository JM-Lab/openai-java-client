package kr.jm.openai;

import kr.jm.openai.dto.OpenAiChatCompletionsResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class OpenAiChatCompletions implements OpenAiChatCompletionsInterface {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Getter
    private final OpenAiApiConf openAiApiConf;

    public OpenAiChatCompletions(OpenAiApiConf openAiApiConf) {
        this.openAiApiConf = openAiApiConf;
    }

    public OpenAiChatCompletions(String openaiApiKey) {
        this(new OpenAiApiConf("https://api.openai.com/v1/chat/completions", openaiApiKey));
    }

    @Override
    public Class<OpenAiChatCompletionsResponse> getResponseClass() {
        return OpenAiChatCompletionsResponse.class;
    }
}
