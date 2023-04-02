
package kr.jm.openai.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Message {

    private Role role;
    private String content;

}
