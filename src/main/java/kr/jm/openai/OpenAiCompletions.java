package kr.jm.openai;

import kr.jm.openai.dto.OpenAiCompletionsRequest;
import kr.jm.openai.dto.OpenAiCompletionsResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class OpenAiCompletions implements
        OpenAiCompletionsInterface<OpenAiCompletionsRequest, OpenAiCompletionsResponse> {

    private final Logger log = LoggerFactory.getLogger(getClass());


    private final OpenAiApiConf openAiApiConf;

    public OpenAiCompletions(OpenAiApiConf openAiApiConf) {
        this.openAiApiConf = openAiApiConf;
    }

    public OpenAiCompletions(String openAiApiKey) {
        this(new OpenAiApiConf("https://api.openai.com/v1/completions", openAiApiKey));
    }

    @Override
    public Class<OpenAiCompletionsResponse> getResponseClass() {
        return OpenAiCompletionsResponse.class;
    }
}
