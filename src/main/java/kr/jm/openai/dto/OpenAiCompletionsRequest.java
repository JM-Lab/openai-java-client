package kr.jm.openai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
@Data
public class OpenAiCompletionsRequest implements DefaultOpenAiCompletionsRequestInterface {
    String model;
    String prompt;
    Integer maxTokens;
    Double temperature;
    Double topP;
    Integer n;
    Boolean stream;
    Integer logprobs;
    Boolean echo;
    List<String> stop;
    Double presencePenalty;
    Double frequencyPenalty;
    Integer bestOf;
    Map<String, Integer> logitBias;
    String user;

}
