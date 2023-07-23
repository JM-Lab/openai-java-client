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
public class OpenAiCompletionsRequest implements DefaultOpenAiCompletionsRequestInterface {
    private String model;
    private String prompt;
    private String suffix;
    @JsonProperty("max_tokens")
    @JsonAlias("maxTokens")
    private Integer maxTokens;
    private Double temperature;
    @JsonProperty("top_p")
    @JsonAlias("topP")
    private Double topP;
    private Integer n;
    private Boolean stream;
    private Integer logprobs;
    private Boolean echo;
    private List<String> stop;
    @JsonProperty("presence_penalty")
    @JsonAlias("presencePenalty")
    private Double presencePenalty;
    @JsonProperty("frequency_penalty")
    @JsonAlias("frequencyPenalty")
    private Double frequencyPenalty;
    @JsonProperty("best_of")
    @JsonAlias("bestOf")
    private Integer bestOf;
    @JsonProperty("logit_bias")
    @JsonAlias("logitBias")
    private Map<String, Integer> logitBias;
    private String user;

}
