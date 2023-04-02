
package kr.jm.openai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
public class OpenAiCompletionsResponse {

    private List<CompletionChoice> choices;
    private Long created;
    private String id;
    private String model;
    private String object;
    private Usage usage;

}
