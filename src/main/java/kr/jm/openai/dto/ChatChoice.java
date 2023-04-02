
package kr.jm.openai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ChatChoice {

    private String finishReason;
    private Long index;
    private Message message;

}
