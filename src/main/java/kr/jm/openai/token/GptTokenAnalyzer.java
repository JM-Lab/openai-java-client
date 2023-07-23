package kr.jm.openai.token;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GptTokenAnalyzer {

    private final Map<Integer, String> partCache;
    private final Pattern pattern;
    private final Encoding encoding;

    public GptTokenAnalyzer(EncodingType encodingType) {
        this.encoding = Encodings.newLazyEncodingRegistry().getEncoding(encodingType);
        this.pattern = Pattern.compile("�|ு|്|்");
        this.partCache = new WeakHashMap<>();
    }

    public List<Integer> getTokenIds(String prompt) {
        return encoding.encodeOrdinary(prompt);
    }

    public int getTokenCount(String prompt) {
        return getTokenIds(prompt).size();
    }

    private List<String> getTokenStrings(List<Integer> tokenIds) {
        return tokenIds.stream().map(tokenId -> partCache.computeIfAbsent(tokenId,
                integer -> encoding.decode(List.of(integer)))).collect(Collectors.toList());
    }

    public TokenAnalysis analysis(String prompt) {
        return makeTokenAnalysis(prompt, getTokenIds(prompt), new ArrayList<>(), new ArrayList<>());
    }

    private TokenAnalysis makeTokenAnalysis(String prompt, List<Integer> tokenIds, List<Integer> partTokenCounts,
            List<String> readableParts) {
        String subPrompt = prompt;
        int tempTokenCount = 0;
        for (String tokenString : getTokenStrings(tokenIds)) {
            if (pattern.matcher(tokenString).find()) {
                tempTokenCount++;
            } else {
                int endIndex = subPrompt.indexOf(tokenString);
                if (tempTokenCount > 0) {
                    addReadablePart(partTokenCounts, tempTokenCount, readableParts, subPrompt.substring(0, endIndex));
                    tempTokenCount = 0;
                }
                addReadablePart(partTokenCounts, 1, readableParts, tokenString);
                subPrompt = subPrompt.substring(endIndex + tokenString.length());
            }
        }
        if (tempTokenCount > 0)
            addReadablePart(partTokenCounts, tempTokenCount, readableParts, subPrompt);
        return new TokenAnalysis(prompt, tokenIds, partTokenCounts, readableParts);
    }

    private void addReadablePart(List<Integer> partTokenCounts, int tempTokenCount, List<String> readableParts,
            String readablePart) {
        partTokenCounts.add(tempTokenCount);
        readableParts.add(readablePart);
    }

}
