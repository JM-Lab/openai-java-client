package kr.jm.openai.token;

import lombok.Value;

import java.util.List;

@Value
public class TokenAnalysis {
    String prompt;
    List<Integer> tokenIds;
    List<Integer> partTokenCounts;
    List<String> readableParts;

    public int getTokenCount() {
        return this.tokenIds.size();
    }

    public int getPromptLength() {
        return this.prompt.length();
    }
}
