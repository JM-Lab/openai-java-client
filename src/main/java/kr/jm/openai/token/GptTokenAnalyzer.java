package kr.jm.openai.token;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GptTokenAnalyzer {

    private final Pattern pattern;
    private final Encoding encoding;

    public GptTokenAnalyzer(EncodingType encodingType) {
        this.encoding = Encodings.newLazyEncodingRegistry().getEncoding(encodingType);
        this.pattern = Pattern.compile("�|ு|്|்");
    }

    public List<Integer> getTokenIds(String prompt) {
        return encoding.encodeOrdinary(prompt);
    }

    public int getTokenCount(String prompt) {
        return getTokenIds(prompt).size();
    }

    private List<String> getTokenStrings(List<Integer> tokenIds) {
        return tokenIds.stream().map(List::of).map(encoding::decode).collect(Collectors.toList());
    }

    public TokenAnalysis analysis(String prompt) {
        String subPrompt = prompt;
        List<Integer> tokenIds = getTokenIds(prompt);
        List<String> readableParts = new ArrayList<>();
        List<Integer> partTokenCounts = new ArrayList<>();
        List<String> tokenStrings = getTokenStrings(tokenIds);
        int tempTokenCount = 0;
        for (String tokenString : tokenStrings) {
            if (pattern.matcher(tokenString).find() || !subPrompt.contains(tokenString)) {
                tempTokenCount++;
            } else {
                int endIndex = subPrompt.indexOf(tokenString);
                if (tempTokenCount > 0) {
                    partTokenCounts.add(tempTokenCount);
                    readableParts.add(subPrompt.substring(0, endIndex));
                    tempTokenCount = 0;
                }
                partTokenCounts.add(1);
                readableParts.add(tokenString);
                subPrompt = subPrompt.substring(endIndex + tokenString.length());
            }
        }

        if (tempTokenCount > 0) {
            partTokenCounts.add(tempTokenCount);
            readableParts.add(subPrompt);
        }

        return new TokenAnalysis(prompt, tokenIds, readableParts, partTokenCounts);
    }
}
