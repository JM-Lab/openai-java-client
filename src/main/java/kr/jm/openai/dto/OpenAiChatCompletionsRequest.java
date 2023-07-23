package kr.jm.openai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
@Data
public class OpenAiChatCompletionsRequest implements DefaultOpenAiCompletionsRequestInterface {
    private String model;
    private List<Message> messages;
    private Double temperature;
    @JsonProperty("top_p")
    @JsonAlias("topP")
    private Double topP;
    private Integer n;
    private Boolean stream;
    private List<String> stop;
    @JsonProperty("max_tokens")
    @JsonAlias("maxTokens")
    private Integer maxTokens;
    @JsonProperty("presence_penalty")
    @JsonAlias("presencePenalty")
    Double presencePenalty;
    @JsonProperty("frequency_penalty")
    @JsonAlias("frequencyPenalty")
    Double frequencyPenalty;
    @JsonProperty("logit_bias")
    @JsonAlias("logitBias")
    Map<String, Integer> logitBias;
    private String user;

}
